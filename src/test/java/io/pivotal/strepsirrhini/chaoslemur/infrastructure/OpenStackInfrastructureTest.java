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
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class OpenStackInfrastructureTest {

    private final DirectorUtils directorUtils = mock(DirectorUtils.class);

    private final Member member = new Member("test-id", "test-deployment", "test-job", "test-name");

    private final NovaApi novaApi = mock(NovaApi.class);

    private final ServerApi serverApi = mock(ServerApi.class);

    private final OpenStackInfrastructure infrastructure = new OpenStackInfrastructure(this.directorUtils, this.novaApi);

    @Test
    public void destroy() throws DestructionException {
        when(this.novaApi.getConfiguredRegions()).thenReturn(new HashSet<>(Arrays.asList("test-region-1", "test-region-2")));

        this.infrastructure.destroy(this.member);

        verify(this.serverApi, times(2)).stop("test-id");
    }

    @Before
    public void setUp() throws Exception {
        when(this.novaApi.getServerApi(any(String.class))).thenReturn(this.serverApi);
    }

}
