/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.reporter;

/**
 * An abstraction for interfacing with Reporters in different ways
 */
public interface Reporter {
    /**
     * Sends an Event to an Event Recorder
     *
     * @param title   The title of the Event
     * @param message The message of the Event
     */
    void sendEvent(String title, String message);

}
