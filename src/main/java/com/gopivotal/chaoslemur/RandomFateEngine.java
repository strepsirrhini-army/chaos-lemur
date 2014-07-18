/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
final class RandomFateEngine implements FateEngine {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final String defaultProbability;

    private final Environment environment;

    private final Random random;

    @Autowired
    RandomFateEngine(@Value("${default.probability:0.5}") Float defaultProbability, Environment environment,
                     Random random) {
        this.defaultProbability = defaultProbability.toString();
        this.environment = environment;
        this.random = random;

        this.logger.info("Default probability: {}", defaultProbability);
    }

    @Override
    public Boolean shouldDie(Member member) {
        Float probability = Float.parseFloat(this.environment.getProperty(getKey(member), this.defaultProbability));
        return this.random.nextFloat() < probability;
    }

    private String getKey(Member member) {
        return String.format("%s.probability", member.getGroup());
    }
}
