/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.infrastructure;

import com.gopivotal.chaoslemur.Member;

import java.util.HashSet;
import java.util.Set;

final class SimpleInfrastructure implements Infrastructure {

    @Override
    public Set<Member> getMembers() {
        Set<Member> members = new HashSet<>();

        members.add(new Member("id-1", "name-1", "group-1"));
        members.add(new Member("id-2", "name-2", "group-2"));
        members.add(new Member("id-3", "name-3", "group-3"));

        return members;
    }

    @Override
    public void destroy(Member member) throws DestructionException {
    }

}
