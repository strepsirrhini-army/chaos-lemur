/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import io.pivotal.xd.chaoslemur.infrastructure.Infrastructure;
import io.pivotal.xd.chaoslemur.reporter.Reporter;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public final class DestroyerControllerTest {

    private final Infrastructure infrastructure = mock(Infrastructure.class);

    private final FateEngine fateEngine = mock(FateEngine.class);

    private final Reporter reporter = mock(Reporter.class);

    private final ExecutorService executor = mock(ExecutorService.class);

    private final MockMvc mockMvc = standaloneSetup(new Destroyer(this.reporter, this.infrastructure, "",
            this.fateEngine, this.executor)).build();

    private final Member member = new Member("test-id", "test-name", "test-group");

    private final Set<Member> members = new HashSet<>();

    @Test
    public void destroyWhenPaused() throws Exception {
        Destroyer destroyer = new Destroyer(this.reporter, this.infrastructure, "", this.fateEngine, this.executor);

        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"paused\"}"))
                .andExpect(status().isOk());
        destroyer.destroy();

        verify(this.infrastructure, times(0)).destroy(member);
    }

    @Test
    public void state() throws Exception {
        this.mockMvc.perform(get("/state"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":\"running\"}"));
    }

    @Test
    public void destroy() throws Exception {
        members.add(member);

        when(this.infrastructure.getMembers()).thenReturn(members);
        when(this.fateEngine.shouldDie(member)).thenReturn(true);

        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"destroying\"}"))
                .andExpect(status().isAccepted());
    }

    @Test
    public void pause() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"paused\"}"))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"paused\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void run() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"running\"}"))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"running\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void invalidValue() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"foo\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void invalidKey() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"foo\":\"bar\"}"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void invalidURL() throws Exception {
        this.mockMvc.perform(post("/state?status=paused"))
                .andExpect(status().is4xxClientError());
    }

}
