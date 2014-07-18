/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

/**
 * Represents a running instance
 */
public final class Member {
    private final String group;

    public Member(String group) {
        this.group = group;
    }

    public String getGroup() {
        return this.group;
    }
}
