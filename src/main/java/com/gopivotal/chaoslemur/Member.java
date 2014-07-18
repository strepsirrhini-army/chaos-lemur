/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

/**
 * Represents a running instance
 */
public final class Member {

    private final String group;


    /**
     * Creates an instance
     * @param group the group the {@link Member} belongs to
     */
    public Member(String group) {
        this.group = group;
    }


    /**
     * Returns the group the {@link Member} belongs to
     * @return the group the {@link Member} belongs to
     */
    public String getGroup() {
        return this.group;
    }

    @Override
    public String toString() {
        return String.format("[group: %s]", this.group);
    }
}
