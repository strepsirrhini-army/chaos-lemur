/*
 * Copyright 2014-2018 the original author or authors.
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

import io.pivotal.strepsirrhini.chaoslemur.Member;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class AbstractDirectorUtilsInfrastructureTest {

    private final Set<String> deployments = new HashSet<>();

    private final DirectorUtils directorUtils = mock(DirectorUtils.class);

    private final StubDirectorUtilsInfrastructure infrastructure = new StubDirectorUtilsInfrastructure(this.directorUtils);

    private final Set<Map<String, String>> vms1 = new HashSet<>();

    private final Set<Map<String, String>> vms2 = new HashSet<>();

    {
        this.deployments.add("deployment1");
        this.deployments.add("deployment2-uuid");
    }

    {
        Map<String, String> vm = new HashMap<>();
        vm.put("cid", "cid1");
        vm.put("job", "job1");
        vm.put("index", "1");

        this.vms1.add(vm);
    }

    {
        Map<String, String> vm = new HashMap<>();
        vm.put("cid", "cid2");
        vm.put("job", "job2-partition-uuid");
        vm.put("index", "2");

        this.vms2.add(vm);
    }

    @Test
    public void getMembers() {
        when(this.directorUtils.getDeployments()).thenReturn(this.deployments);
        when(this.directorUtils.getVirtualMachines("deployment1")).thenReturn(this.vms1);
        when(this.directorUtils.getVirtualMachines("deployment2-uuid")).thenReturn(this.vms2);

        Set<Member> expected = new HashSet<>();
        expected.add(new Member("cid1", "deployment1", "job1", "job1/1"));
        expected.add(new Member("cid2", "deployment2", "job2", "job2-partition-uuid/2"));

        Set<Member> actual = this.infrastructure.getMembers();

        assertEquals(expected, actual);
    }


    private static final class StubDirectorUtilsInfrastructure extends AbstractDirectorUtilsInfrastructure {

        StubDirectorUtilsInfrastructure(DirectorUtils directorUtils) {
            super(directorUtils);
        }

        @Override
        public void destroy(Member member) throws DestructionException {

        }

    }

}
