/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import io.pivotal.xd.chaoslemur.infrastructure.DestructionException;
import io.pivotal.xd.chaoslemur.infrastructure.Infrastructure;
import io.pivotal.xd.chaoslemur.reporter.Reporter;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

@RestController
final class Destroyer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Executor executor;

    private final FateEngine fateEngine;

    private final Infrastructure infrastructure;

    private final Object monitor = new Object();

    private final Reporter reporter;

    private volatile State state = State.RUNNING;

    @Autowired
    Destroyer(Reporter reporter, Infrastructure infrastructure, @Value("${schedule:0 0 * * * *}") String
            schedule, Executor executor, FateEngine fateEngine) {
        this.reporter = reporter;
        this.executor = executor;
        this.fateEngine = fateEngine;
        this.infrastructure = infrastructure;

        this.logger.info("Destruction schedule: {}", schedule);
    }

    /**
     * Trigger method for destruction of members. This method is invoked on a schedule defined by the cron statement
     * stored in the {@code schedule} configuration property.  By default this schedule is {@code 0 0 * * * *}.
     */
    @Scheduled(cron = "${schedule:0 0 * * * *}")
    public void destroy() {
        synchronized (this.monitor) {
            if (this.state.equals(State.PAUSED)) {
                this.logger.info("Chaos Lemur paused");
                return;
            }

            doDestroy();
        }
    }

    @RequestMapping(method = RequestMethod.POST, value = "/state")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void changeState(@RequestBody Map<String, String> payload) {
        State targetState = State.parse(payload);

        this.executor.execute(() -> {
            synchronized (Destroyer.this.monitor) {
                if (targetState == State.RUNNING || targetState == State.PAUSED) {
                    Destroyer.this.state = targetState;
                    return;
                }

                doDestroy();
            }
        });
    }

    @RequestMapping(method = RequestMethod.GET, value = "/state")
    Map<String, State> reportState() {
        synchronized (this.monitor) {
            Map<String, State> message = new HashMap<>();
            message.put("status", this.state);
            return message;
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void handleParsingException() {
    }

    private void doDestroy() {
        State previousState = this.state;
        this.state = State.DESTROYING;

        UUID identifier = UUID.randomUUID();
        this.logger.info("{} Beginning run...", identifier);

        List<Member> destroyedMembers = new CopyOnWriteArrayList<>();

        this.infrastructure.getMembers().parallelStream().forEach((member) -> {
            if (this.fateEngine.shouldDie(member)) {
                try {
                    this.logger.debug("{} Destroying: {}", identifier, member);
                    this.infrastructure.destroy(member);
                    this.logger.info("{} Destroyed: {}", identifier, member);
                    destroyedMembers.add(member);
                } catch (DestructionException e) {
                    this.logger.warn("{} Destroy failed: {} ({})", identifier, member, e.getMessage());
                }
            }
        });

        this.reporter.sendEvent(title(identifier), message(destroyedMembers));
        this.state = previousState;
    }

    private String message(List<Member> members) {
        int size = members.size();

        String SPACE = "\u00A0";
        String BULLET = "\u2022";

        String s = "\n";
        s += size + English.plural(" VM", size) + " destroyed:\n";
        s += members.stream().sorted().map((member) -> SPACE + SPACE + BULLET + SPACE + member.getName()).collect
                (Collectors.joining("\n"));

        return s;
    }

    private String title(UUID identifier) {
        return String.format("Chaos Lemur Destruction (%s)", identifier);
    }

}
