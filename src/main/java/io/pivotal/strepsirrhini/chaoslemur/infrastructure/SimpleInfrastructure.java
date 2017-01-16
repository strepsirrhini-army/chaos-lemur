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

import io.pivotal.strepsirrhini.chaoslemur.Member;

import java.util.HashSet;
import java.util.Set;

final class SimpleInfrastructure implements Infrastructure {

    @Override
    public void destroy(Member member) throws DestructionException {
    }

    @Override
    public Set<Member> getMembers() {
        Set<Member> members = new HashSet<>();

        members.add(new Member("id-1", "deployment-1", "job-1", "name-1"));
        members.add(new Member("id-2", "deployment-2", "job-2", "name-2"));
        members.add(new Member("id-3", "deployment-3", "job-3", "name-3"));

        return members;
    }

}
