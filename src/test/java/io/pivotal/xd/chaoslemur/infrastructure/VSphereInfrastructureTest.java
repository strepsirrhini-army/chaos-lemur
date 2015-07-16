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

import com.vmware.vim25.LocalizedMethodFault;
import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import io.pivotal.xd.chaoslemur.Member;
import org.junit.Test;

import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class VSphereInfrastructureTest {

    private final Member member = new Member("test-id", "test-deployment", "test-job", "test-name");

    private final VirtualMachine virtualMachine = mock(VirtualMachine.class);

    private final DirectorUtils directorUtils = mock(DirectorUtils.class);

    private final InventoryNavigator inventoryNavigator = mock(InventoryNavigator.class);

    private final InventoryNavigatorFactory inventoryNavigatorFactory = mock(InventoryNavigatorFactory.class);

    private final Task task = mock(Task.class);

    private final TaskInfo taskInfo = mock(TaskInfo.class);

    private final LocalizedMethodFault localizedMethodFault = mock(LocalizedMethodFault.class);

    private final VSphereInfrastructure infrastructure = new VSphereInfrastructure(this.directorUtils, this
            .inventoryNavigatorFactory);

    @Test
    public void destroy() throws DestructionException, IOException {
        when(this.inventoryNavigatorFactory.create()).thenReturn(this.inventoryNavigator);
        when(this.inventoryNavigator.searchManagedEntity("VirtualMachine", "test-id")).thenReturn(this.virtualMachine);
        when(this.virtualMachine.powerOffVM_Task()).thenReturn(this.task);
        when(this.virtualMachine.destroy_Task()).thenReturn(this.task);
        when(this.task.getTaskInfo()).thenReturn(this.taskInfo);
        when(this.taskInfo.getState()).thenReturn(TaskInfoState.success);

        this.infrastructure.destroy(this.member);

        verify(this.virtualMachine).powerOffVM_Task();
    }

    @Test(expected = DestructionException.class)
    public void taskFailure() throws DestructionException, IOException {
        when(this.inventoryNavigatorFactory.create()).thenReturn(this.inventoryNavigator);
        when(this.inventoryNavigator.searchManagedEntity("VirtualMachine", "test-id")).thenReturn(this.virtualMachine);
        when(this.virtualMachine.powerOffVM_Task()).thenReturn(this.task);
        when(this.task.getTaskInfo()).thenReturn(this.taskInfo);
        when(this.taskInfo.getState()).thenReturn(TaskInfoState.error);
        when(this.taskInfo.getError()).thenReturn(this.localizedMethodFault);

        this.infrastructure.destroy(this.member);

    }
}
