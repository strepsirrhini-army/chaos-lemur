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

import java.util.Map;
import java.util.Set;

interface DirectorUtils {

    /**
     * Returns a list of deployments on the Director
     *
     * @return a list of deployments on the Director
     */
    Set<String> getDeployments();

    /**
     * Returns a list of VMs in a deployment
     *
     * @param deployment the deployment
     * @return a list of VMs in a deployment
     */
    Set<Map<String, String>> getVirtualMachines(String deployment);

}
