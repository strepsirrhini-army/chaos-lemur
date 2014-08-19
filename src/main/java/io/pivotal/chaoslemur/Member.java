/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.chaoslemur;

/**
 * Represents a running instance
 */
public final class Member implements Comparable<Member>{

    private final String id;

    private final String name;

    private final String group;

    /**
     * Creates an instance
     *
     * @param group the group the {@link Member} belongs to
     */
    public Member(String id, String name, String group) {
        this.id = id;
        this.name = name;
        this.group = group;
    }

    /**
     * Returns the ID of the {@link Member}
     *
     * @return the ID of the {@link Member}
     */
    public String getId() {
        return this.id;
    }

    /**
     * Returns the name of the {@link Member}
     *
     * @return the name of the {@link Member}
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the group the {@link Member} belongs to
     *
     * @return the group the {@link Member} belongs to
     */
    public String getGroup() {
        return this.group;
    }

    @Override
    public int compareTo(Member member) {
        return this.name.compareTo(member.getName());
    }

    @Override
    public String toString() {
        return String.format("[id: %s, name: %s, group: %s]", this.id, this.name, this.group);
    }
}
