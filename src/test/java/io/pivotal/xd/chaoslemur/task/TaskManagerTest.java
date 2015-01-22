/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.task;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public final class TaskManagerTest {

    private final TaskResourceAssembler resourceAssembler = new TaskResourceAssembler();

    private final TaskManager taskManager = new TaskManager(this.resourceAssembler);

    private final MockMvc mockMvc = standaloneSetup(this.taskManager).build();

    private final Task task1 = this.taskManager.create(Trigger.MANUAL);

    private final Task task2 = this.taskManager.create(Trigger.SCHEDULED);

    @Test
    public void create() {
        assertEquals(Trigger.MANUAL, this.task1.getTrigger());
        assertEquals(Trigger.SCHEDULED, this.task2.getTrigger());
        assertEquals(this.task1.getId() + 1, (long) this.task2.getId());
    }

    @Test
    public void read() throws Exception {
        this.mockMvc.perform(get("/task/0").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.links").value(hasSize(1)))
                .andExpect(jsonPath("$.links[?(@.rel == 'self')].href").value("http://localhost/task/0"));
    }

    @Test
    public void readNull() throws Exception {
        this.mockMvc.perform(get("/task/99").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void readAll() throws Exception {
        this.mockMvc.perform(get("/task").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(hasSize(2)))
                .andExpect(jsonPath("$[*].links[?(@.rel == 'self')].href")
                        .value(hasItems("http://localhost/task/0", "http://localhost/task/1")));
    }
}
