/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.state;

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
    public void state() throws Exception {
        this.mockMvc.perform(get("/state"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":\"STARTED\"}"));
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
    public void invalidValue() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"foo\"}"))
                .andExpect(status().isBadRequest());

        assertNull(this.restControllerStateProvider.setState);
    }

    @Test
    public void invalidKey() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"foo\":\"bar\"}"))
                .andExpect(status().isBadRequest());

        assertNull(this.restControllerStateProvider.setState);
    }

    private static final class StubRestControllerStateProvider extends AbstractRestControllerStateProvider {

        private volatile State setState;

        @Override
        protected void set(State state) {
            this.setState = state;
        }

        @Override
        public State get() {
            return State.STARTED;
        }
    }

}
