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
    public void getId() {
        assertTrue(this.task.getId().equals(123L));
    }

    @Test
    public void getStatus() {
        assertEquals(this.task.getStatus(), TaskStatus.IN_PROGRESS);
    }

    @Test
    public void getStart() {
        assertTrue(this.task.getStart().isBefore(LocalDateTime.now().plusSeconds(5)));
    }

    @Test
    public void getTrigger() {
        assertTrue(this.task.getTrigger().equals(Trigger.MANUAL));
    }

    @Test
    public void stop() {
        this.task.stop();
        assertEquals(this.task.getStatus(), TaskStatus.COMPLETE);
    }

}