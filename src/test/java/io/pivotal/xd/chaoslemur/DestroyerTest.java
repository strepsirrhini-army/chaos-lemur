/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import io.pivotal.xd.chaoslemur.infrastructure.DestructionException;
import io.pivotal.xd.chaoslemur.infrastructure.Infrastructure;
import io.pivotal.xd.chaoslemur.reporter.Reporter;
import io.pivotal.xd.chaoslemur.state.State;
import io.pivotal.xd.chaoslemur.state.StateProvider;
import io.pivotal.xd.chaoslemur.task.Task;
import io.pivotal.xd.chaoslemur.task.TaskRepository;
import io.pivotal.xd.chaoslemur.task.TaskUriBuilder;
import io.pivotal.xd.chaoslemur.task.Trigger;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URI;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public final class DestroyerTest {

    private final ArgumentCaptor<Runnable> runnableCaptor = ArgumentCaptor.forClass(Runnable.class);

    private final ExecutorService executorService = mock(ExecutorService.class);

    private final FateEngine fateEngine = mock(FateEngine.class);

    private final Future future = mock(Future.class);

    private final Infrastructure infrastructure = mock(Infrastructure.class);

    private final Reporter reporter = mock(Reporter.class);

    private final StateProvider stateProvider = mock(StateProvider.class);

    private final TaskRepository taskRepository = mock(TaskRepository.class);

    private final TaskUriBuilder taskUriBuilder = mock(TaskUriBuilder.class);

    private final Destroyer destroyer = new Destroyer(this.executorService, this.fateEngine, this.infrastructure,
            this.reporter, this.stateProvider, "", this.taskRepository, this.taskUriBuilder);

    private final MockMvc mockMvc = standaloneSetup(this.destroyer).build();

    private final Member member1 = new Member("test-id-1", "test-name-1", "test-group");

    private final Member member2 = new Member("test-id-2", "test-name-2", "test-group");

    private final Set<Member> members = Stream.of(this.member1, this.member2).collect(Collectors.toSet());

    @Before
    public void members() {
        when(this.infrastructure.getMembers()).thenReturn(this.members);
        when(this.fateEngine.shouldDie(this.member1)).thenReturn(true);
        when(this.fateEngine.shouldDie(this.member2)).thenReturn(false);
        when(this.taskRepository.create(Trigger.SCHEDULED)).thenReturn(new Task(1L, Trigger.SCHEDULED));
    }

    @Before
    @SuppressWarnings("unchecked")
    public void executor() throws ExecutionException, InterruptedException {
        when(this.executorService.submit(any(Runnable.class))).thenReturn(this.future);
        when(this.future.get()).thenReturn(true);
    }

    @Test
    public void destroy() throws DestructionException {
        this.destroyer.destroy();

        runRunnables();

        verify(this.infrastructure).destroy(this.member1);
        verify(this.infrastructure, never()).destroy(this.member2);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void destroyFail() throws DestructionException {
        doThrow(new DestructionException()).when(this.infrastructure).destroy(this.member1);

        this.destroyer.destroy();

        verify(this.infrastructure, never()).destroy(this.member1);
    }

    @Test
    public void destroyError() throws DestructionException, IllegalStateException {
        doThrow(new IllegalStateException()).when(this.infrastructure).destroy(this.member1);

        this.destroyer.destroy();

        verify(this.infrastructure, never()).destroy(this.member1);
    }

    @Test
    public void destroyWhenStopped() throws DestructionException {
        when(this.stateProvider.get()).thenReturn(State.STOPPED);

        this.destroyer.destroy();

        verify(this.infrastructure, never()).getMembers();
    }

    @Test
    public void manualDestroy() throws Exception {
        Task task = new Task(2L, Trigger.MANUAL);
        when(this.taskRepository.create(Trigger.MANUAL)).thenReturn(task);
        when(this.taskUriBuilder.getUri(task)).thenReturn(URI.create("http://foo.com"));

        this.mockMvc.perform(post("/chaos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"event\":\"destroy\"}"))
                .andExpect(status().isAccepted())
                .andExpect(header().string("Location", "http://foo.com"));

        runRunnables();

        verify(this.infrastructure).destroy(this.member1);
        verify(this.infrastructure, never()).destroy(this.member2);
    }

    @Test
    public void invalidKey() throws Exception {
        this.mockMvc.perform(post("/chaos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"foo\":\"destroy\"}"))
                .andExpect(status().isBadRequest());

        verify(this.infrastructure, never()).destroy(this.member1);
    }

    @Test
    public void invalidValue() throws Exception {
        this.mockMvc.perform(post("/chaos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"event\":\"foo\"}"))
                .andExpect(status().isBadRequest());

        verify(this.infrastructure, never()).destroy(this.member1);
    }

    private void runRunnables() {
        verify(this.executorService, atMost(1)).execute(this.runnableCaptor.capture());
        this.runnableCaptor.getAllValues().forEach(Runnable::run);

        verify(this.executorService, times(2)).submit(this.runnableCaptor.capture());
        this.runnableCaptor.getAllValues().forEach(Runnable::run);
    }

}
