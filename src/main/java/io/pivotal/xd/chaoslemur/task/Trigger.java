/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.task;

/**
 * Valid ways for an event to start
 * <ul>
 *  <li>{@link #MANUAL}</li>
 *  <li>{@link #SCHEDULED}</li>
 *  </ul>
 */
public enum Trigger {

    /**
     * The event was started manually, i.e. from outside Chaos Lemur
     */
    MANUAL,

    /**
     * The event was started according to Chaos Lemur's schedule
     */
    SCHEDULED
}
