/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.datadog;

final class NoOpDataDog implements DataDog {

    @Override
    public void sendEvent(String title, String message) {
    }

}
