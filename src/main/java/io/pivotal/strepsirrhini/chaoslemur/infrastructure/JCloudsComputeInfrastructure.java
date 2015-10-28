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

import io.pivotal.strepsirrhini.chaoslemur.Member;

import java.util.Set;

import org.jclouds.ContextBuilder;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

final class JCloudsComputeInfrastructure extends AbstractDirectorUtilsInfrastructure {

	private final static Logger logger=LoggerFactory.getLogger(JCloudsComputeInfrastructure.class.getName());
	
	
    private final NovaApi novaApi;
    private final Set<String> regions;

	

    public JCloudsComputeInfrastructure(DirectorUtils directorUtils,String endpoint, String tenant,
			String username, String password, String proxyhost, String proxyport) {
    	super(directorUtils);
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        logger.debug("initialize jclouds compute API");
        String provider = "openstack-nova";
        String identity = tenant+":"+username; // tenantName:userName
        String credential = password;
        
        
        logger.debug("logging as {}",identity);

        novaApi = ContextBuilder.newBuilder(provider)
                .endpoint(endpoint)
                .credentials(identity, credential)
                .modules(modules)
                .buildApi(NovaApi.class);
        regions = novaApi.getConfiguredRegions();

	}

	@Override
    public void destroy(Member member) throws DestructionException {
        try {
            logger.debug("destroy vm {}",member);
        	String vmId=member.getId();
        	ServerApi serverApi = novaApi.getServerApi(regions.iterator().next()); //FIXME: multi az ?
        	logger.debug("found member {}",vmId);
        	serverApi.stop(vmId);
        	//serverApi.delete(vmId);
        } catch (Exception e) {
            throw new DestructionException(String.format("Unable to destroy %s", member), e);
        }
    }

}
