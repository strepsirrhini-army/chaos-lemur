/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import io.pivotal.xd.chaoslemur.infrastructure.Infrastructure;
import io.pivotal.xd.chaoslemur.reporter.Reporter;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

    private final Executor executor = new SyncTaskExecutor();

    private final Destroyer destroyer = new Destroyer(this.reporter, this.infrastructure, "", this.executor,
            this.fateEngine);

    private final MockMvc mockMvc = standaloneSetup(this.destroyer).build();

    private final Member member1 = new Member("test-id-1", "test-name-1", "test-group");

    private final Member member2 = new Member("test-id-2", "test-name-2", "test-group");

    private final Set<Member> members = Stream.of(this.member1, this.member2).collect(Collectors.toSet());

    @Before
    public void members() {
        when(this.infrastructure.getMembers()).thenReturn(this.members);
        when(this.fateEngine.shouldDie(this.member1)).thenReturn(true);
        when(this.fateEngine.shouldDie(this.member2)).thenReturn(false);
    }

    @Test
    public void destroyWhenPaused() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"paused\"}"))
                .andExpect(status().isAccepted());

        this.destroyer.destroy();

        verify(this.infrastructure, times(0)).destroy(this.member1);
        verify(this.infrastructure, times(0)).destroy(this.member2);
    }

    @Test
    public void state() throws Exception {
        this.mockMvc.perform(get("/state"))
                .andExpect(status().isOk())
                .andExpect(content().string("{\"status\":\"RUNNING\"}"));
    }

    @Test
    public void destroy() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"destroying\"}"))
                .andExpect(status().isAccepted());

        verify(this.infrastructure).destroy(this.member1);
        verify(this.infrastructure, times(0)).destroy(this.member2);
    }

    @Test
    public void pause() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"paused\"}"))
                .andExpect(status().isAccepted());
        this.mockMvc.perform(get("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"PAUSED\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void run() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"running\"}"))
                .andExpect(status().isAccepted());
        this.mockMvc.perform(get("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"RUNNING\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void invalidValue() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"foo\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidKey() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"foo\":\"bar\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void invalidURL() throws Exception {
        this.mockMvc.perform(post("/state?status=paused"))
                .andExpect(status().isUnsupportedMediaType());
    }

}
