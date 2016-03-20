/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.strepsirrhini.chaoslemur;

import io.pivotal.strepsirrhini.chaoslemur.infrastructure.DestructionException;
import io.pivotal.strepsirrhini.chaoslemur.infrastructure.Infrastructure;
import io.pivotal.strepsirrhini.chaoslemur.reporter.Event;
import io.pivotal.strepsirrhini.chaoslemur.reporter.Reporter;
import io.pivotal.strepsirrhini.chaoslemur.state.State;
import io.pivotal.strepsirrhini.chaoslemur.state.StateProvider;
import io.pivotal.strepsirrhini.chaoslemur.task.Task;
import io.pivotal.strepsirrhini.chaoslemur.task.TaskRepository;
import io.pivotal.strepsirrhini.chaoslemur.task.TaskUriBuilder;
import io.pivotal.strepsirrhini.chaoslemur.task.Trigger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@RestController
final class Destroyer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Boolean dryRun;

    private final ExecutorService executorService;

    private final FateEngine fateEngine;

    private final Infrastructure infrastructure;

    private final Reporter reporter;

    private final StateProvider stateProvider;

    private final TaskRepository taskRepository;

    private final TaskUriBuilder taskUriBuilder;

    @Autowired
    Destroyer(@Value("${dryRun:false}") Boolean dryRun,
              ExecutorService executorService,
              FateEngine fateEngine,
              Infrastructure infrastructure,
              Reporter reporter,
              StateProvider stateProvider,
              @Value("${schedule:0 0 * * * *}") String schedule,
              TaskRepository taskRepository,
              TaskUriBuilder taskUriBuilder) {
        this.logger.info("Destruction schedule: {}", schedule);

        this.dryRun = dryRun;
        this.executorService = executorService;
        this.fateEngine = fateEngine;
        this.infrastructure = infrastructure;
        this.reporter = reporter;
        this.stateProvider = stateProvider;
        this.taskRepository = taskRepository;
        this.taskUriBuilder = taskUriBuilder;
    }

    /**
     * Trigger method for destruction of members. This method is invoked on a schedule defined by the cron statement stored in the {@code schedule} configuration property.  By default this schedule is
     * {@code 0 0 * * * *}.
     */
    @Scheduled(cron = "${schedule:0 0 * * * *}")
    public void destroy() {
        if (State.STOPPED == this.stateProvider.get()) {
            this.logger.info("Chaos Lemur stopped");
            return;
        }

        doDestroy(this.taskRepository.create(Trigger.SCHEDULED));
    }

    @RequestMapping(method = RequestMethod.POST, value = "/chaos", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> eventRequest(@RequestBody Map<String, String> payload) {
        String value = payload.get("event");

        if (value == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        HttpHeaders responseHeaders = new HttpHeaders();

        if ("destroy".equals(value.toLowerCase())) {
            Task task = this.taskRepository.create(Trigger.MANUAL);
            this.executorService.execute(() -> doDestroy(task));
            responseHeaders.setLocation(this.taskUriBuilder.getUri(task));
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(responseHeaders, HttpStatus.ACCEPTED);
    }

    private void doDestroy(Task task) {
        List<Member> destroyedMembers = new CopyOnWriteArrayList<>();
        UUID identifier = UUID.randomUUID();

        this.logger.info("{} Beginning run...", identifier);

        this.infrastructure.getMembers().stream()
            .map(member -> this.executorService.submit(() -> {
                if (this.fateEngine.shouldDie(member)) {
                    try {
                        this.logger.debug("{} Destroying: {}", identifier, member);

                        if (this.dryRun) {
                            this.logger.info("{} Destroyed (Dry Run): {}", identifier, member);
                        } else {
                            this.infrastructure.destroy(member);
                            this.logger.info("{} Destroyed: {}", identifier, member);
                        }

                        destroyedMembers.add(member);
                    } catch (DestructionException e) {
                        this.logger.warn("{} Destroy failed: {}", identifier, member, e);
                    }
                }
            }))
            .forEach(future -> {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    this.logger.warn("{} Failed to destroy member", identifier, e);
                }
            });

        this.reporter.sendEvent(new Event(identifier, destroyedMembers));

        task.stop();
    }

}
