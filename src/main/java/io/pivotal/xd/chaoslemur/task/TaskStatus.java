/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.task;

/**
 * Valid states for a {@code Task}
 * <ul>
 *  <li>{@link #COMPLETE}</li>
 *  <li>{@link #IN_PROGRESS}</li>
 *  </ul>
 */
public enum TaskStatus {

    /**
     * The {@code Task} is complete
     */
    COMPLETE,

    /**
     * The {@code Task} is in progress
     */
    IN_PROGRESS
}
