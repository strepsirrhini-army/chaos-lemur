/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.infrastructure;

import com.gopivotal.chaoslemur.Member;

import java.util.HashSet;
import java.util.Set;

/**
 * A simple implementation of {@link Infrastructure}. Returns static values for all methods.
 */
public final class SimpleInfrastructure implements Infrastructure {

    @Override
    public Set<Member> getMembers() {
        Set<Member> members = new HashSet<>();

        members.add(new Member());
        members.add(new Member());
        members.add(new Member());

        return members;
    }
}
