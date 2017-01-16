/*
 * Copyright 2014-2017 the original author or authors.
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

import org.cloudfoundry.identity.client.UaaContext;
import org.cloudfoundry.identity.client.UaaContextFactory;
import org.cloudfoundry.identity.client.token.GrantType;
import org.cloudfoundry.identity.client.token.TokenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Created by allomov on 1/11/17.
 */
@Component
@ConditionalOnProperty(name = "bosh.auth.type", havingValue = BoshAuthType.UAA)
public class BoshOAuthHeaderRequestInterceptor implements ClientHttpRequestInterceptor {

    @Autowired
    private InfrastructureConfiguration config;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {

        if (config.boshAuthType.equals(BoshAuthType.UAA)) {
            HttpHeaders headers = request.getHeaders();
            String token = getUaaToken();
            headers.add("Authorization", "bearer " + token);
        }

        ClientHttpResponse response = execution.execute(request, body);
        return response;
    }

    private String getUaaToken() {
        UaaContextFactory factory = UaaContextFactory.factory(config.getUaaUri())
            .authorizePath("/oauth/authorize")
            .tokenPath("/oauth/token");

        TokenRequest passwordGrant = factory.tokenRequest()
            .setClientId(config.uaaClientId)
            .setClientSecret(config.uaaClientSecret)
            .setGrantType(GrantType.PASSWORD)
            .setUsername(config.boshUser)
            .setPassword(config.boshPassword)
            .setScopes(null);

        UaaContext context = factory.authenticate(passwordGrant);

        String token = context.getToken().getValue();
        return token;
    }
}
