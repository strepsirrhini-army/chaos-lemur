/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.infrastructure;

import com.gopivotal.chaoslemur.DestructionException;
import com.gopivotal.chaoslemur.Member;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple implementation of {@link Infrastructure}. Returns static values for all methods.
 */
@Component
public final class SimpleInfrastructure implements Infrastructure {

    @Override
    public Set<Member> getMembers() {
        Set<Member> members = new HashSet<>();

        members.add(new Member("group-1", "name-1"));
        members.add(new Member("group-2", "name-2"));
        members.add(new Member("group-3", "name-3"));

        return members;
    }

    @Override
    public void destroy(Member member) throws DestructionException {
        //
    }
}
