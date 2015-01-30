/*
 * Copyright 2014-2015 the original author or authors.
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

package io.pivotal.xd.chaoslemur.task;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;

/**
 * Tracks the status of a long-running event.
 */
public final class Task {

    private final Long id;

    private final LocalDateTime startDate = LocalDateTime.now();

    private volatile TaskStatus status = TaskStatus.IN_PROGRESS;

    private final Trigger trigger;

    /**
     * Create a new task
     * @param id Unique id for the task
     * @param trigger The {@code Trigger} for the task
     */
    public Task(Long id, Trigger trigger) {
        this.id = id;
        this.trigger = trigger;
    }

    /**
     * Returns the id of the task
     *
     * @return the id of the task
     */
    @JsonIgnore
    public Long getId() {
        return this.id;
    }

    /**
     * Returns the status of the task
     *
     * @return the {@code TaskStatus} of the task
     */
    public TaskStatus getStatus() {
        return this.status;
    }

    /**
     * Returns the start time of the task
     *
     * @return the start time of the task
     */
    public LocalDateTime getStart() {
        return this.startDate;
    }

    /**
     * Returns the trigger of the task
     *
     * @return the {@code Trigger} of the task
     */
    public Trigger getTrigger() {
        return this.trigger;
    }

    /**
     * Stops the task.
     */
    public void stop() {
        this.status = TaskStatus.COMPLETE;
    }

}
