/*
 * Copyright 2014-2016 the original author or authors.
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

package io.pivotal.strepsirrhini.chaoslemur.infrastructure;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.mock.http.client.MockClientHttpRequest;
import org.springframework.mock.http.client.MockClientHttpResponse;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ContentTypeClientHttpRequestInterceptorTest {

    private final byte[] body = new byte[0];

    private final ClientHttpRequestExecution execution = mock(ClientHttpRequestExecution.class);

    private final ContentTypeClientHttpRequestInterceptor interceptor = new ContentTypeClientHttpRequestInterceptor();

    private final MockClientHttpRequest request = new MockClientHttpRequest();

    private final MockClientHttpResponse response = new MockClientHttpResponse(this.body, HttpStatus.OK);

    @Test
    public void intercept() throws IOException {
        when(this.execution.execute(this.request, this.body)).thenReturn(this.response);

        this.interceptor.intercept(this.request, this.body, this.execution);

        assertEquals(MediaType.APPLICATION_JSON, this.response.getHeaders().getContentType());
    }
}
