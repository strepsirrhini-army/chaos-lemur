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
import com.amazonaws.services.ec2.model.Tag;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import io.pivotal.xd.chaoslemur.Member;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class AwsInfrastructure implements Infrastructure {

    private static final Pattern DEPLOYMENT_PATTERN = Pattern.compile("(.+)-[^-]+");

    private static final Pattern JOB_PATTERN = Pattern.compile("(.+)-partition-.+");

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
                String deployment = null;
                String job = null;
                String name = null;

                for (Tag tag : instance.getTags()) {
                    if (tag.getKey().equals("deployment")) {
                        deployment = normalize(tag, DEPLOYMENT_PATTERN);
                    } else if (tag.getKey().equals("job")) {
                        job = normalize(tag, JOB_PATTERN);
                    } else if (tag.getKey().equals("Name")) {
                        name = tag.getValue();
                    }
                }

                members.add(new Member(id, deployment, job, name));
            }
        });

        return members;
    }

    @Override
    public void destroy(Member member) throws DestructionException {
        List<String> terminate = new ArrayList<>();
        terminate.add(member.getId());
        TerminateInstancesRequest tir = new TerminateInstancesRequest(terminate);
        this.amazonEC2.terminateInstances(tir);
    }

    private String normalize(Tag tag, Pattern pattern) {
        Matcher matcher = pattern.matcher(tag.getValue());

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return tag.getValue();
        }
    }

}
