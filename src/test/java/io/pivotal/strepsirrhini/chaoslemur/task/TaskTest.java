/*
 * Copyright 2014-2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.pivotal.strepsirrhini.chaoslemur.task;

import org.junit.Test;

import java.time.LocalDateTime;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public final class TaskTest {

    private final Task task = new Task(123L, Trigger.MANUAL);

    @Test
    public void stop() {
        this.task.stop();
        assertEquals(this.task.getStatus(), TaskStatus.COMPLETE);
    }

    @Test
    public void test() {
        assertEquals((Long) 123L, this.task.getId());
        assertEquals(TaskStatus.IN_PROGRESS, this.task.getStatus());
        assertTrue(this.task.getStart().isBefore(LocalDateTime.now().plusSeconds(5)));
        assertEquals(Trigger.MANUAL, this.task.getTrigger());
    }

}
