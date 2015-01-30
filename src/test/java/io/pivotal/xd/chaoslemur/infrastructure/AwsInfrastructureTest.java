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

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import io.pivotal.xd.chaoslemur.Member;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class AwsInfrastructureTest {

    private final Member member = new Member("test-id", "test-deployment", "test-job", "test-name");

    private final AmazonEC2 amazonEC2 = mock(AmazonEC2.class);

    private final AwsInfrastructure infrastructure = new AwsInfrastructure(this.amazonEC2, "test-vpc");

    @Test
    public void getMembers() throws Exception {
        when(this.amazonEC2.describeInstances(
                        new DescribeInstancesRequest().withFilters(Arrays.asList(
                                new Filter().withName("vpc-id").withValues("test-vpc"),
                                new Filter().withName("tag-key").withValues("director"))))
        ).thenReturn(
                new DescribeInstancesResult().withReservations(Arrays.asList(
                        new Reservation().withInstances(Arrays.asList(
                                new Instance().withInstanceId("test-id").withTags(Arrays.asList(
                                        new Tag("deployment", "test-deployment-857142ab878465bb9e7b"),
                                        new Tag("job", "test-job-partition-us-east-1e"),
                                        new Tag("Name", "test-name-partition-us-east-1e/1")))))))
        );

        Set<Member> members = this.infrastructure.getMembers();

        assertEquals(1, members.size());

        Member member = members.iterator().next();
        assertEquals("test-id", member.getId());
        assertEquals("test-deployment", member.getDeployment());
        assertEquals("test-job", member.getJob());
        assertEquals("test-name-partition-us-east-1e/1", member.getName());
    }

    @Test
    public void destroy() throws Exception {
        this.infrastructure.destroy(this.member);
        verify(this.amazonEC2).terminateInstances(terminateInstancesRequest());
    }

    private TerminateInstancesRequest terminateInstancesRequest() {
        return new TerminateInstancesRequest().withInstanceIds(member.getId());
    }


}
