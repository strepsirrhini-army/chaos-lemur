/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.reporter;

import org.junit.Test;

public final class NoOpReporterTest {

    @Test
    public void sendEvent() {
        Reporter reporter = new NoOpReporter();
        reporter.sendEvent("test-title", "test-message");
    }
}
