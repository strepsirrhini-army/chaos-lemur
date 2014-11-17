/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Random;

/**
 * Main entry point and configuration class
 */
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class ApplicationConfiguration {

    /**
     * Start method
     *
     * @param args command line argument
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(ApplicationConfiguration.class, args);
    }

    @Bean
    Random random() {
        return new SecureRandom();
    }

    @Bean
    RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean(destroyMethod = "shutdown")
    ThreadPoolExecutorFactoryBean executor() {
        ThreadPoolExecutorFactoryBean factoryBean = new ThreadPoolExecutorFactoryBean();
        factoryBean.setQueueCapacity(0);
        factoryBean.setCorePoolSize(5);
        factoryBean.setMaxPoolSize(20);
        factoryBean.setThreadNamePrefix("destroyer-");

        return factoryBean;
    }

    @Bean
    ObjectMapper objectMapper() {
        return new ObjectMapper()
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

}