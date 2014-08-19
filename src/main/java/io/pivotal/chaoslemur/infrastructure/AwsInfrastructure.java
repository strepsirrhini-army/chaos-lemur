/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.chaoslemur.infrastructure;

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Filter;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import io.pivotal.chaoslemur.Member;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

final class AwsInfrastructure implements Infrastructure {

    private final AmazonEC2 amazonEC2;

    private final String vpcId;

    AwsInfrastructure(AmazonEC2 amazonEC2, String vpcId) {
        this.amazonEC2 = amazonEC2;
        this.vpcId = vpcId;
    }

    @Override
    public Set<Member> getMembers() {
        Set<Member> members = new HashSet<>();

        DescribeInstancesRequest request = new DescribeInstancesRequest().withFilters(
                new Filter().withName("vpc-id").withValues(this.vpcId),
                new Filter().withName("tag-key").withValues("director")
        );

        DescribeInstancesResult result = this.amazonEC2.describeInstances(request);

        result.getReservations().stream().forEach((reservation) -> {
            for (Instance instance : reservation.getInstances()) {
                String id = instance.getInstanceId();
                String name = null;
                String group = null;

                for (Tag tag : instance.getTags()) {
                    if (tag.getKey().equals("job")) {
                        group = tag.getValue();
                    } else if (tag.getKey().equals("Name")) {
                        name = tag.getValue();
                    }
                }

                members.add(new Member(id, name, group));
            }
        });

        return members;
    }

    @Override
    public void destroy(Member member) throws DestructionException {
        List<String> terminate = new ArrayList<String>();
        terminate.add(member.getId());
        TerminateInstancesRequest tir = new TerminateInstancesRequest(terminate);
        this.amazonEC2.terminateInstances(tir);
    }

}
