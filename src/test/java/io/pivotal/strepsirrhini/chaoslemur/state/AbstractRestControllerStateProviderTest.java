/*
 * Copyright 2014-2017 the original author or authors.
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

package io.pivotal.strepsirrhini.chaoslemur.state;

import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public final class AbstractRestControllerStateProviderTest {

    private final StubRestControllerStateProvider restControllerStateProvider = new StubRestControllerStateProvider();

    private final MockMvc mockMvc = standaloneSetup(this.restControllerStateProvider).build();

    @Test
    public void invalidKey() throws Exception {
        this.mockMvc.perform(post("/state")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"foo\":\"bar\"}"))
            .andExpect(status().isBadRequest());

        assertNull(this.restControllerStateProvider.setState);
    }

    @Test
    public void invalidValue() throws Exception {
        this.mockMvc.perform(post("/state")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"foo\"}"))
            .andExpect(status().isBadRequest());

        assertNull(this.restControllerStateProvider.setState);
    }

    @Test
    public void pause() throws Exception {
        this.mockMvc.perform(post("/state")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"stopped\"}"))
            .andExpect(status().isOk());

        assertEquals(State.STOPPED, this.restControllerStateProvider.setState);
    }

    @Test
    public void run() throws Exception {
        this.mockMvc.perform(post("/state")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"status\":\"started\"}"))
            .andExpect(status().isOk());

        assertEquals(State.STARTED, this.restControllerStateProvider.setState);
    }

    @Test
    public void state() throws Exception {
        this.mockMvc.perform(get("/state"))
            .andExpect(status().isOk())
            .andExpect(content().string("{\"status\":\"STARTED\"}"));
    }

    private static final class StubRestControllerStateProvider extends AbstractRestControllerStateProvider {

        private volatile State setState;

        @Override
        public State get() {
            return State.STARTED;
        }

        @Override
        protected void set(State state) {
            this.setState = state;
        }
    }

}
