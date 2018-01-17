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

package io.pivotal.strepsirrhini.chaoslemur.reporter;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public final class DataDogReporterTest {

    private static final String URI = "https://app.datadoghq.com/api/v1/events?api_key=apiKey&application_key=appKey";

    private Reporter dataDog;

    private MockRestServiceServer mockServer;

    @Test
    public void badSendEvent() {
        this.mockServer.expect(requestTo(URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withBadRequest());

        this.dataDog.sendEvent(new Event(UUID.randomUUID(), Collections.emptyList()));

        this.mockServer.verify();
    }

    @Test
    public void sendEvent() {
        this.mockServer.expect(requestTo(URI))
            .andExpect(method(HttpMethod.POST))
            .andRespond(withSuccess("resultSuccess", MediaType.TEXT_PLAIN));

        this.dataDog.sendEvent(new Event(UUID.randomUUID(), Collections.emptyList()));

        this.mockServer.verify();
    }

    @Before
    public void setup() {
        RestTemplate restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
        this.dataDog = new DataDogReporter("apiKey", "appKey", restTemplate, new String[]{"key:value"});
    }

}
