/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.infrastructure;

import io.pivotal.xd.chaoslemur.Member;

import java.util.HashSet;
import java.util.Set;

final class SimpleInfrastructure implements Infrastructure {

    @Override
    public Set<Member> getMembers() {
        Set<Member> members = new HashSet<>();

        members.add(new Member("id-1", "deployment-1", "job-1", "name-1"));
        members.add(new Member("id-2", "deployment-2", "job-2", "name-2"));
        members.add(new Member("id-3", "deployment-3", "job-3", "name-3"));

        return members;
    }

    @Override
    public void destroy(Member member) throws DestructionException {
    }

}
