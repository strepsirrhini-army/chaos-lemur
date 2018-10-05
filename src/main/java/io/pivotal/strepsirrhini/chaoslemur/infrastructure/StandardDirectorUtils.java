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

package io.pivotal.strepsirrhini.chaoslemur.infrastructure;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
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
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import javax.net.ssl.SSLContext;
import java.net.URI;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@ConditionalOnProperty("director.host")
final class StandardDirectorUtils implements DirectorUtils {
	private static final Logger log = LoggerFactory.getLogger(StandardDirectorUtils.class);
	
    private final RestTemplate restTemplate;

    private final URI root;

    @Autowired
    StandardDirectorUtils(@Value("${director.host}") String host,
                          @Value("${director.username}") String username,
                          @Value("${director.password}") String password,
                          Set<ClientHttpRequestInterceptor> interceptors) throws GeneralSecurityException {
        this(createRestTemplate(host, username, password, interceptors), UriComponentsBuilder.newInstance().scheme("https").host(host).port(25555).build().toUri());
    }

    StandardDirectorUtils(RestTemplate restTemplate, URI root) {
        this.restTemplate = restTemplate;
        this.root = root;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<String> getDeployments() {
        URI deploymentsUri = UriComponentsBuilder.fromUri(this.root)
            .path("deployments")
            .build().toUri();

        List<Map<String, String>> deployments = this.restTemplate.getForObject(deploymentsUri, List.class);

        return deployments.stream()
            .map(deployment -> deployment.get("name"))
            .collect(Collectors.toSet());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Map<String, String>> getVirtualMachines(String deployment) {
        URI vmsUri = UriComponentsBuilder.fromUri(this.root)
            .pathSegment("deployments", deployment, "vms")
            .build().toUri();

        return this.restTemplate.getForObject(vmsUri, Set.class);
    }

    private static RestTemplate createRestTemplate(String host, String username, String password, Set<ClientHttpRequestInterceptor> interceptors) throws GeneralSecurityException {
        
    	String directorUaaBearerToken = getBoshDirectorUaaToken(host,username,password);
    	
		SSLContext sslContext = SSLContexts.custom()
	            .loadTrustMaterial(null, new TrustSelfSignedStrategy())
	            .useTLS()
	            .build();

	        SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, new AllowAllHostnameVerifier());

	        Header auth = new BasicHeader("Authorization", String.format("Bearer %s", directorUaaBearerToken));
	        Header accept = new BasicHeader("Accept", "*/*");
	        List<Header> headers = new ArrayList<Header>();
	        headers.add(auth);
	        headers.add(accept);
	        

	        HttpClient httpClient = HttpClientBuilder.create()
	            .disableRedirectHandling()
	            .setDefaultHeaders(headers)
	            .setSSLSocketFactory(connectionFactory)
	            .build();
        
        
        RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
        restTemplate.getInterceptors().addAll(interceptors);

        return restTemplate;
    }
    
    
    private static String getBoshDirectorUaaToken(String host, String directorName, String password) throws  GeneralSecurityException{
    	
        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, new TrustSelfSignedStrategy())
                .useTLS()
                .build();

            SSLConnectionSocketFactory connectionFactory = new SSLConnectionSocketFactory(sslContext, new AllowAllHostnameVerifier());

            HttpClient httpClient = HttpClientBuilder.create()
                .disableRedirectHandling()
                .setSSLSocketFactory(connectionFactory)
                .build();
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(httpClient));
            
            MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
            String base64Passowrd = encodePassword(directorName,password);
            headers.add("Authorization", "Basic " + base64Passowrd);
            headers.add("Content-Type", "application/x-www-form-urlencoded");

            String postArgs = "grant_type=client_credentials";
            
            HttpEntity<String> requestEntity = new HttpEntity<String>(postArgs,headers);
            String uri = "https://" + host + ":8443/oauth/token";
            UaaToken response = restTemplate.postForObject(uri, requestEntity, UaaToken.class);

            log.info("Uaa token:" + response);
    		return response.getAccess_token();
    }
    
	public static String encodePassword(String username, String passowrd){
		String plainCreds = username + ":" + passowrd;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
		return new String(base64CredsBytes);
	}


}
