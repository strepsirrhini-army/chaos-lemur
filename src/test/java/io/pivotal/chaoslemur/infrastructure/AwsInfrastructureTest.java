/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.chaoslemur.infrastructure;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import io.pivotal.chaoslemur.Member;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class AwsInfrastructureTest {

    private final Member member = new Member("test-id", "test-name", "test-group");

    private final AmazonEC2 amazonEC2 = mock(AmazonEC2.class);

    private final AwsInfrastructure infrastructure = new AwsInfrastructure(this.amazonEC2, "test-vpc");

    @Test
    public void getMembers() throws Exception {
        when(this.amazonEC2.describeInstances(request())).thenReturn(result());
        assertTrue(this.infrastructure.getMembers().toString().equals("[[id: test-id, name: test-name, " +
                "group: test-group]]"));
    }

    @Test
    public void destroy() throws Exception {
        this.infrastructure.destroy(this.member);
        verify(this.amazonEC2).terminateInstances(terminateInstancesRequest());
    }

    private TerminateInstancesRequest terminateInstancesRequest() {
        return new TerminateInstancesRequest().withInstanceIds(member.getId());
    }

    private DescribeInstancesRequest request() {
        return new DescribeInstancesRequest().withFilters(filters());
    }

    private Collection<Filter> filters() {
        return Arrays.asList(
                new Filter().withName("vpc-id").withValues("test-vpc"),
                new Filter().withName("tag-key").withValues("director"));
    }

    private DescribeInstancesResult result() {
        return new DescribeInstancesResult().withReservations(reservations());
    }

    private Collection<Reservation> reservations() {
        return Arrays.asList(new Reservation().withInstances(instances()));
    }

    private Collection<Instance> instances() {
        return Arrays.asList(new Instance().withInstanceId("test-id").withTags(tags()));
    }


    private Collection<Tag> tags() {
        return Arrays.asList(new Tag("Name", "test-name"), new Tag("job", "test-group"));
    }
}