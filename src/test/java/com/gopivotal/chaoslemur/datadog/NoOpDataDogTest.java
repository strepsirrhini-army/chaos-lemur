/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.datadog;

import org.junit.Test;

public final class NoOpDataDogTest {

    @Test
    public void sendEvent() {
        DataDog dataDog = new NoOpDataDog();
        dataDog.sendEvent("test-title", "test-message");
    }
}