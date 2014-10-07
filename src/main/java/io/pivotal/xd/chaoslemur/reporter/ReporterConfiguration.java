/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.reporter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class ReporterConfiguration {

    @Bean
    @ConditionalOnProperty("dataDog.apiKey")
    Reporter dataDogReporter(@Value("${dataDog.apiKey}") String apiKey, @Value("${dataDog.appKey}") String appKey,
                            RestTemplate restTemplate) {
        return new DataDogReporter(apiKey, appKey, restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(Reporter.class)
    Reporter noOpReporter() {
        return new NoOpReporter();
    }

}
