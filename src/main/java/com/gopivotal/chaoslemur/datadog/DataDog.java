/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.datadog;

/**
 * An abstraction for interfacing with DataDog in different ways
 */
public interface DataDog {
    /**
     * Sends an Event to DataDog
     *
     * @param title   The title of the Event
     * @param message The message of the Event
     */
    void sendEvent(String title, String message);

}
