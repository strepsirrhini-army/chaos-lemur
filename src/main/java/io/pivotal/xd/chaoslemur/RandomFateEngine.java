/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Random;

@Component
final class RandomFateEngine implements FateEngine {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String[] blacklist;

    private final String defaultProbability;

    private final Environment environment;

    private final Random random;

    @Autowired
    RandomFateEngine(@Value("${blacklist:}") String[] blacklist, @Value("${default.probability:0.2}") Float
            defaultProbability, Environment environment, Random random) {
        this.blacklist = blacklist;
        this.defaultProbability = defaultProbability.toString();
        this.environment = environment;
        this.random = random;

        this.logger.info("Blacklist: {}", StringUtils.arrayToCommaDelimitedString(blacklist));
        this.logger.info("Default probability: {}", defaultProbability);
    }

    @Override
    public Boolean shouldDie(Member member) {
        if (isBlacklisted(member)) {
            return false;
        }

        Float probability = new Precedence<String>()
                .candidate(() -> getProbability(member.getJob()))
                .candidate(() -> getProbability(member.getDeployment()))
                .candidate(this.defaultProbability)
                .get(Float::parseFloat);

        return this.random.nextFloat() < probability;
    }

    private boolean isBlacklisted(Member member) {
        return Arrays.stream(this.blacklist)
                .anyMatch(s -> member.getDeployment().equalsIgnoreCase(s) || member.getJob().equalsIgnoreCase(s));
    }

    private String getProbability(String name) {
        return this.environment.getProperty(getProbabilityKey(name));
    }

    private String getProbabilityKey(String name) {
        return String.format("%s.probability", name);
    }
}
