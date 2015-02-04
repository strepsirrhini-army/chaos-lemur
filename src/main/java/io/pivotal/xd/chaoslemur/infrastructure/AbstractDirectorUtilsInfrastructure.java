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

import io.pivotal.xd.chaoslemur.Member;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

abstract class AbstractDirectorUtilsInfrastructure implements Infrastructure {

    private static final Pattern DEPLOYMENT_PATTERN = Pattern.compile("(.+)-[^-]+");

    private static final Pattern JOB_PATTERN = Pattern.compile("(.+)-partition-.+");

    private final DirectorUtils directorUtils;

    protected AbstractDirectorUtilsInfrastructure(DirectorUtils directorUtils) {
        this.directorUtils = directorUtils;
    }

    @Override
    public final Set<Member> getMembers() {
        Set<Member> members = new HashSet<>();

        this.directorUtils.getDeployments().stream().forEach(deployment -> {
            String normalizedDeployment = normalizeDeployment(deployment);

            this.directorUtils.getVirtualMachines(deployment).stream().forEach(virtualMachine -> {
                String id = virtualMachine.get("cid");
                String job = normalizeJob(virtualMachine.get("job"));
                String name = String.format("%s/%s", virtualMachine.get("job"), virtualMachine.get("index"));

                members.add(new Member(id, normalizedDeployment, job, name));
            });
        });

        return members;
    }

    private String normalizeDeployment(String value) {
        return normalize(value, DEPLOYMENT_PATTERN);
    }

    private String normalizeJob(String value) {
        return normalize(value, JOB_PATTERN);
    }

    private static String normalize(String value, Pattern pattern) {
        Matcher matcher = pattern.matcher(value);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return value;
        }
    }
}
