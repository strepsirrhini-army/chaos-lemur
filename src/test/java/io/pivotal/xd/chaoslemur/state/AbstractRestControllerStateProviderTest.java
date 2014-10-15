/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.state;

import io.pivotal.xd.chaoslemur.ControllerSupport;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public final class AbstractRestControllerStateProviderTest {

    private final StubRestControllerStateProvider restControllerStateProvider = new StubRestControllerStateProvider();

    private final MockMvc mockMvc = standaloneSetup(this.restControllerStateProvider)
            .setHandlerExceptionResolvers(createExceptionResolver()).build();

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
                .andExpect(status().isAccepted());

        assertEquals(State.STOPPED, this.restControllerStateProvider.setState);
    }

    @Test
    public void run() throws Exception {
        this.mockMvc.perform(post("/state")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"started\"}"))
                .andExpect(status().isAccepted());

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
