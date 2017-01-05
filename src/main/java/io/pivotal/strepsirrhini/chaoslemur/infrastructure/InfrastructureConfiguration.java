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

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import org.jclouds.Constants;
import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Properties;

@Configuration
class InfrastructureConfiguration {

    @Autowired
    DirectorUtils directorUtils;

    @Bean
    @ConditionalOnProperty("aws.accessKeyId")
    AmazonEC2Client amazonEC2(@Value("${aws.accessKeyId}") String accessKeyId,
                              @Value("${aws.secretAccessKey}") String secretAccessKey,
                              @Value("${aws.region:us-east-1}") String regionName) {

        AmazonEC2Client amazonEC2Client = new AmazonEC2Client(new BasicAWSCredentials(accessKeyId, secretAccessKey));
        Region region = Region.getRegion(Regions.fromName(regionName));
        amazonEC2Client.setEndpoint(region.getServiceEndpoint("ec2"));

        return amazonEC2Client;
    }

    @Bean
    @ConditionalOnBean(AmazonEC2.class)
    AwsInfrastructure awsInfrastructure(DirectorUtils directorUtils, AmazonEC2 amazonEC2) {
        return new AwsInfrastructure(directorUtils, amazonEC2);
    }

    @Bean
    @ConditionalOnProperty("vsphere.host")
    StandardInventoryNavigatorFactory inventoryNavigatorFactory(@Value("${vsphere.host}") String host,
                                                                @Value("${vsphere.username}") String username,
                                                                @Value("${vsphere.password}") String password) throws MalformedURLException {

        return new StandardInventoryNavigatorFactory(host, username, password);
    }

    @Bean
    @ConditionalOnProperty("openstack.endpoint")
    NovaApi novaApi(@Value("${openstack.endpoint}") String endpoint,
                    @Value("${openstack.tenant}") String tenant,
                    @Value("${openstack.username}") String username,
                    @Value("${openstack.password}") String password) {

        String identity = String.format("%s:%s", tenant, username);

        // see https://issues.apache.org/jira/browse/JCLOUDS-816
        Properties overrides = new Properties();
        overrides.put(Constants.PROPERTY_TRUST_ALL_CERTS, "true");
        overrides.put(Constants.PROPERTY_RELAX_HOSTNAME, "true");

        return ContextBuilder.newBuilder("openstack-nova")
            .endpoint(endpoint)
            .credentials(identity, password)
            .modules(Collections.singleton(new SLF4JLoggingModule()))
            .overrides(overrides)
            .buildApi(NovaApi.class);
    }

    @Bean
    @ConditionalOnBean(NovaApi.class)
    OpenStackInfrastructure openStackInfrastructure(DirectorUtils directorUtils, NovaApi novaApi) {
        return new OpenStackInfrastructure(directorUtils, novaApi);
    }

    @Bean
    @ConditionalOnProperty("simple.infrastructure")
    SimpleInfrastructure simpleInfrastructure() {
        return new SimpleInfrastructure();
    }

    @Bean
    @ConditionalOnBean(InventoryNavigatorFactory.class)
    VSphereInfrastructure vSphereInfrastructure(DirectorUtils directorUtils, InventoryNavigatorFactory inventoryNavigatorFactory) {
        return new VSphereInfrastructure(directorUtils, inventoryNavigatorFactory);
    }

}
