/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

/**
 * Represents a running instance
 */
public final class Member implements Comparable<Member> {

    private final String id;

    private final String deployment;

    private final String name;

    private final String job;

    /**
     * Creates an instance
     *
     * @param id         the ID of the {@link Member}
     * @param deployment the deployment the {@link Member} belongs to
     * @param job        the job the {@link Member} belongs to
     * @param name       the name of the {@link Member}
     */
    public Member(String id, String deployment, String job, String name) {
        this.id = id;
        this.deployment = deployment;
        this.job = job;
        this.name = name;
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
     * Returns the deployment the {@link Member} belongs to
     *
     * @return the deployment the {@link Member} belongs to
     */
    public String getDeployment() {
        return this.deployment;
    }

    /**
     * Returns the job the {@link Member} belongs to
     *
     * @return the job the {@link Member} belongs to
     */
    public String getJob() {
        return this.job;
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
    public int compareTo(Member member) {
        return this.name.compareTo(member.getName());
    }

    @Override
    public String toString() {
        return String.format("[id: %s, deployment: %s, job: %s, name: %s]", this.id, this.deployment, this.job, this
                .name);
    }
}
