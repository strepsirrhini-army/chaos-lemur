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

import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import io.pivotal.strepsirrhini.chaoslemur.Member;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public final class AwsInfrastructureTest {

    private final AmazonEC2 amazonEC2 = mock(AmazonEC2.class);

    private final DirectorUtils directorUtils = mock(DirectorUtils.class);

    private final AwsInfrastructure infrastructure = new AwsInfrastructure(this.directorUtils, this.amazonEC2);

    private final Member member = new Member("test-id", "test-deployment", "test-job", "test-name");

    @Test
    public void destroy() throws Exception {
        this.infrastructure.destroy(this.member);
        verify(this.amazonEC2).terminateInstances(terminateInstancesRequest());
    }

    private TerminateInstancesRequest terminateInstancesRequest() {
        return new TerminateInstancesRequest().withInstanceIds(this.member.getId());
    }


}
