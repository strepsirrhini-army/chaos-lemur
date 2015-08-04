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

package io.pivotal.strepsirrhini.chaoslemur.task;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletWebRequest;

import java.net.URISyntaxException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public final class TaskResourceAssemblerTest {

    private final TaskResourceAssembler resourceAssembler = new TaskResourceAssembler();

    Task task = new Task(0L, Trigger.MANUAL);

    @Before
    public final void requestContextSetup() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletWebRequest(request, response));
    }

    @After
    public void requestContextTearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    public void toResource() {
        Resource<Task> resource = this.resourceAssembler.toResource(this.task);

        assertSame(this.task, resource.getContent());
        assertEquals(new Link("http://localhost/task/0", "self"), resource.getLink("self"));
    }

    @Test
    public void getUri() throws URISyntaxException {
        assertEquals("http://localhost/task/0", this.resourceAssembler.getUri(this.task).toString());
    }
}
