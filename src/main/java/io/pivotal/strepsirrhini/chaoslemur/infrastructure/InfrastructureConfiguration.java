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

package io.pivotal.strepsirrhini.chaoslemur.infrastructure;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;

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
    @ConditionalOnProperty("aws.ec2Region")
    Infrastructure awsInfrastructure(StandardDirectorUtils directorUtils, AmazonEC2 amazonEC2,  @Value("${aws.ec2Region:us-east-1}") String ec2Region) {
        return new AwsInfrastructure(directorUtils, amazonEC2,  ec2Region);
    }

    @Bean
    @ConditionalOnProperty("vsphere.host")
    InventoryNavigatorFactory inventoryNavigatorFactory(@Value("${vsphere.host}") String host,
                                                        @Value("${vsphere.username}") String username,
                                                        @Value("${vsphere.password}") String password)
            throws MalformedURLException {

        return new StandardInventoryNavigatorFactory(host, username, password);
    }

    @Bean
    @ConditionalOnBean(InventoryNavigatorFactory.class)
    Infrastructure vSphereInfrastructure(InventoryNavigatorFactory inventoryNavigatorFactory,
                                         StandardDirectorUtils directorUtils) {
        return new VSphereInfrastructure(directorUtils, inventoryNavigatorFactory);
    }

    @Bean
    @ConditionalOnProperty("simple.infrastructure")
    Infrastructure simpleInfrastructure() {
        return new SimpleInfrastructure();
    }

}
