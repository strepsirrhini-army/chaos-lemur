/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

/**
 * Represents a running instance
 */
public final class Member {

    private final String group;

    private final String name;


    /**
     * Creates an instance
     *
     * @param group the group the {@link Member} belongs to
     */
    public Member(String group, String name) {
        this.group = group;
        this.name = name;
    }


    /**
     * Returns the group the {@link Member} belongs to
     *
     * @return the group the {@link Member} belongs to
     */
    public String getGroup() {
        return this.group;
    }

    /**
     * Returns the name of the {@link Member}
     *
     * @return the name of the {@link Member}
     */
    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return String.format("[group: %s, name: %s]", this.group, this.name);
    }


}
