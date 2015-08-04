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

package io.pivotal.strepsirrhini.chaoslemur.infrastructure;

import com.vmware.vim25.TaskInfo;
import com.vmware.vim25.TaskInfoState;
import com.vmware.vim25.mo.Task;
import com.vmware.vim25.mo.VirtualMachine;
import io.pivotal.strepsirrhini.chaoslemur.Member;
import org.springframework.util.Assert;

import java.io.IOException;
import java.rmi.RemoteException;

final class VSphereInfrastructure extends AbstractDirectorUtilsInfrastructure {

    private final InventoryNavigatorFactory inventoryNavigatorFactory;

    VSphereInfrastructure(DirectorUtils directorUtils, InventoryNavigatorFactory inventoryNavigatorFactory) {
        super(directorUtils);
        this.inventoryNavigatorFactory = inventoryNavigatorFactory;
    }

    @Override
    public void destroy(Member member) throws DestructionException {
        try {
            VirtualMachine virtualMachine = (VirtualMachine) this.inventoryNavigatorFactory.create()
                    .searchManagedEntity("VirtualMachine", member.getId());

            Assert.notNull(virtualMachine, String.format("virtualMachine must not be null for %s", member));

            handleTask(virtualMachine.powerOffVM_Task());
        } catch (InterruptedException | IOException e) {
            throw new DestructionException(String.format("Unable to destroy %s", member), e);
        }
    }

    private void handleTask(Task task) throws DestructionException, InterruptedException, RemoteException {
        task.waitForTask();

        TaskInfo taskInfo = task.getTaskInfo();
        if (TaskInfoState.error == taskInfo.getState()) {
            throw new DestructionException(taskInfo.getError().getLocalizedMessage());
        }
    }
}
