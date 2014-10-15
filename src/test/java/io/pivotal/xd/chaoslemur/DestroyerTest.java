/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import io.pivotal.xd.chaoslemur.infrastructure.DestructionException;
import io.pivotal.xd.chaoslemur.infrastructure.Infrastructure;
import io.pivotal.xd.chaoslemur.reporter.Reporter;
import io.pivotal.xd.chaoslemur.state.State;
import io.pivotal.xd.chaoslemur.state.StateProvider;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public final class DestroyerTest {

    private final FateEngine fateEngine = mock(FateEngine.class);

    private final Infrastructure infrastructure = mock(Infrastructure.class);

    private final Reporter reporter = mock(Reporter.class);

    private final StateProvider stateProvider = mock(StateProvider.class);

    private final Destroyer destroyer = new Destroyer(this.fateEngine, this.infrastructure, this.reporter,
            this.stateProvider, "");

    private final MockMvc mockMvc = standaloneSetup(this.destroyer)
            .setHandlerExceptionResolvers(createExceptionResolver()).build();

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
    public void destroy() throws DestructionException {
        this.destroyer.destroy();

        verify(this.infrastructure).destroy(this.member1);
        verify(this.infrastructure, times(0)).destroy(this.member2);
    }

    @Test
    public void destroyFail() throws DestructionException {
        doThrow(new DestructionException()).when(this.infrastructure).destroy(this.member1);

        this.destroyer.destroy();
    }

    @Test
    public void destroyWhenStopped() throws DestructionException {
        when(stateProvider.get()).thenReturn(State.STOPPED);

        this.destroyer.destroy();

        verify(this.infrastructure, times(0)).getMembers();
    }

    @Test
    public void manualDestroy() throws Exception {
        this.mockMvc.perform(post("/chaos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"event\":\"destroy\"}"))
                .andExpect(status().isAccepted());

        verify(this.infrastructure).destroy(this.member1);
        verify(this.infrastructure, times(0)).destroy(this.member2);
    }

    @Test
    public void invalidKey() throws Exception {
        this.mockMvc.perform(post("/chaos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"foo\":\"destroy\"}"))
                .andExpect(status().isBadRequest());

        verify(this.infrastructure, times(0)).destroy(this.member1);
    }

    @Test
    public void invalidValue() throws Exception {
        this.mockMvc.perform(post("/chaos")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"event\":\"foo\"}"))
                .andExpect(status().isBadRequest());

        verify(this.infrastructure, times(0)).destroy(this.member1);
    }

    private ExceptionHandlerExceptionResolver createExceptionResolver() {
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
            protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod,
                                                                              Exception exception) {
                Method method = new ExceptionHandlerMethodResolver(ControllerSupport.class).resolveMethod(exception);
                return new ServletInvocableHandlerMethod(new ControllerSupport(), method);
            }
        };
        exceptionResolver.afterPropertiesSet();
        return exceptionResolver;
    }

}
