/*
 * Copyright 2014-2016 the original author or authors.
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

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import io.pivotal.strepsirrhini.chaoslemur.Member;

import java.util.ArrayList;
import java.util.List;

final class AwsInfrastructure extends AbstractDirectorUtilsInfrastructure {

    private final AmazonEC2 amazonEC2;

    AwsInfrastructure(DirectorUtils directorUtils, AmazonEC2 amazonEC2) {
        super(directorUtils);
        this.amazonEC2 = amazonEC2;
    }

    @Override
    public void destroy(Member member) throws DestructionException {
        List<String> terminate = new ArrayList<>();
        terminate.add(member.getId());
        TerminateInstancesRequest tir = new TerminateInstancesRequest(terminate);
        this.amazonEC2.terminateInstances(tir);
    }

}