/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.chaoslemur.datadog;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
class DataDogConfiguration {

    @Bean
    @ConditionalOnProperty("dataDog.apiKey")
    DataDog standardDataDog(@Value("${dataDog.apiKey}") String apiKey, @Value("${dataDog.appKey}") String appKey,
                            RestTemplate restTemplate) {
        return new StandardDataDog(apiKey, appKey, restTemplate);
    }

    @Bean
    @ConditionalOnMissingBean(DataDog.class)
    DataDog noOpDataDog() {
        return new NoOpDataDog();
    }

}