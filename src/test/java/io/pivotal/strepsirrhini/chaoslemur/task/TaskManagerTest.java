/*
 * Copyright 2014-2018 the original author or authors.
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
    public void readAll() throws Exception {
        this.mockMvc.perform(get("/task").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").value(hasSize(2)))
            .andExpect(jsonPath("$[*].links[?(@.rel == 'self')].href")
                .value(hasItems("http://localhost/task/0", "http://localhost/task/1")));
    }

    @Test
    public void readNull() throws Exception {
        this.mockMvc.perform(get("/task/99").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound());
    }
}
