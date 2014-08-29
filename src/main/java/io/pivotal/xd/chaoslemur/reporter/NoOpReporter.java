/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.reporter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class NoOpReporter implements Reporter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void sendEvent(String title, String message) {
        this.logger.debug("{}:\n{}", title, message);
    }

}
