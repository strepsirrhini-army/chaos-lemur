/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.infrastructure;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class InfrastructureConfiguration {

    @Bean
    @ConditionalOnProperty("aws.accessKeyId")
    AmazonEC2 amazonEC2(@Value("${aws.accessKeyId}") String accessKeyId,
                        @Value("${aws.secretAccessKey}") String secretAccessKey) {
        return new AmazonEC2Client(new BasicAWSCredentials(accessKeyId, secretAccessKey));
    }

    @Bean
    @ConditionalOnBean(AmazonEC2.class)
    Infrastructure awsInfrastructure(AmazonEC2 amazonEC2, @Value("${aws.vpcId}") String vpcId) {
        return new AwsInfrastructure(amazonEC2, vpcId);
    }

    @Bean
    @ConditionalOnMissingBean(Infrastructure.class)
    Infrastructure simpleInfrastructure() {
        return new SimpleInfrastructure();
    }

}
