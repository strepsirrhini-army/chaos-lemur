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

import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ServiceInstance;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

final class StandardInventoryNavigatorFactory implements InventoryNavigatorFactory {

    private final String password;

    private final URL url;

    private final String username;

    StandardInventoryNavigatorFactory(String host, String username, String password) throws MalformedURLException {
        this.password = password;
        this.url = new URL(String.format("https://%s/sdk", host));
        this.username = username;
    }

    @Override
    public InventoryNavigator create() throws IOException {
        ServiceInstance serviceInstance = new ServiceInstance(this.url, this.username, this.password, true);
        return new InventoryNavigator(serviceInstance.getRootFolder());
    }

}
