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

package io.pivotal.xd.chaoslemur.infrastructure;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import javax.net.ssl.SSLContext;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty("director.host")
final class StandardDirectorUtils implements DirectorUtils {

    private final RestTemplate restTemplate;

    private final URI root;

    @Autowired
    StandardDirectorUtils(@Value("${director.host}") String host,
                          @Value("${director.username}") String username,
                          @Value("${director.password}") String password,
                          Set<ClientHttpRequestInterceptor> interceptors) throws GeneralSecurityException {
        this(createRestTemplate(host, username, password, interceptors),
                UriComponentsBuilder.newInstance().scheme("https").host(host).port(25555).build().toUri());
    }

    StandardDirectorUtils(RestTemplate restTemplate, URI root) {
        this.restTemplate = restTemplate;
        this.root = root;
    }

    private static RestTemplate createRestTemplate(String host, String username, String password,
                                                   Set<ClientHttpRequestInterceptor> interceptors)
            throws GeneralSecurityException {
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host, 25555),
                new UsernamePasswordCredentials(username, password));

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .useTLS()
                .build();

        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext,
                new AllowAllHostnameVerifier());

        HttpClient httpClient = HttpClientBuilder.create()
                .disableRedirectHandling()
                .setDefaultCredentialsProvider(credentialsProvider)
                .setSSLSocketFactory(connectionFactory)
                .build();

        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getInterceptors().addAll(interceptors);

        return restTemplate;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getDeployments() {
        URI deploymentsUri = UriComponentsBuilder.fromUri(this.root).path("deployments").build().toUri();
        List<Map<String, String>> deployments = this.restTemplate.getForObject(deploymentsUri, List.class);

        return deployments.stream().map(deployment -> deployment.get("name")).collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Map<String, String>> getVirtualMachines(String deployment) {
        URI vmsUri = UriComponentsBuilder.fromUri(this.root).pathSegment("deployments", deployment, "vms").build()
                .toUri();

        return this.restTemplate.getForObject(vmsUri, Set.class);
    }

}
