/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.task;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class TaskTest {

    private final Task task = new Task(123L, Trigger.MANUAL);

    @Test
    public void test() {
        assertEquals((Long) 123L, this.task.getId());
        assertEquals(TaskStatus.IN_PROGRESS, this.task.getStatus());
        assertTrue(this.task.getStart().isBefore(LocalDateTime.now().plusSeconds(5)));
        assertEquals(Trigger.MANUAL, this.task.getTrigger());
    }

    @Test
    public void stop() {
        this.task.stop();
        assertEquals(this.task.getStatus(), TaskStatus.COMPLETE);
    }

}
