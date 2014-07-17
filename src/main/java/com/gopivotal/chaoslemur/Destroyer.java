/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Destroys members on a regular schedule.
 */
@Component
@EnableScheduling
public final class Destroyer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    Destroyer(@Value("${schedule:0 0/10 * * * *}") String schedule) {
        this.logger.info("Destruction schedule: {}", schedule);
    }

    /**
     * Trigger method for destruction of members. This method is invoked on a schedule defined by the cron statement
     * stored in the {@code schedule} configuration property.  By default this schedule is {@code 0 0/10 * * * *}.
     */
    @Scheduled(cron = "${schedule:0 0/10 * * * *}")
    public void destroy() {
        this.logger.info("Destructoid initiated");
    }

}