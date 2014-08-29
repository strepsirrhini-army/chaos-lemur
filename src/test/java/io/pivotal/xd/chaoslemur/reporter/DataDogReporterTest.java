/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.reporter;

import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

public final class DataDogReporterTest {

    private static final String URI = "https://app.datadoghq.com/api/v1/events?api_key=apiKey&application_key=appKey";
    private Reporter dataDog;
    private MockRestServiceServer mockServer;

    @Before
    public void setup() {
        RestTemplate restTemplate = new RestTemplate();
        this.mockServer = MockRestServiceServer.createServer(restTemplate);
        this.dataDog = new DataDogReporter("apiKey", "appKey", restTemplate);
    }

    @Test
    public void sendEvent() {
        mockServer.expect(requestTo(URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess("resultSuccess", MediaType.TEXT_PLAIN));

        dataDog.sendEvent("Title", "Message");

        this.mockServer.verify();
    }

    @Test
    public void badSendEvent() {
        mockServer.expect(requestTo(URI))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withBadRequest());

        dataDog.sendEvent("Title", "Message");

        this.mockServer.verify();
    }
}
