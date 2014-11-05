/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.task;

public interface TaskRepository {

    /**
     * Creates a {@code Task}
     *
     * @param trigger The {@code Trigger} type of the task
     * @return The {@code Task}
     */
    Task create(Trigger trigger);
}
