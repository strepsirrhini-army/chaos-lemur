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

package io.pivotal.strepsirrhini.chaoslemur;

import org.springframework.util.Assert;

/**
 * Represents a running instance
 */
public final class Member implements Comparable<Member> {

    private final String deployment;

    private final String id;

    private final String job;

    private final String name;

    /**
     * Creates an instance
     *
     * @param id         the ID of the {@link Member}
     * @param deployment the deployment the {@link Member} belongs to
     * @param job        the job the {@link Member} belongs to
     * @param name       the name of the {@link Member}
     */
    public Member(String id, String deployment, String job, String name) {
        Assert.hasText(id, "id must have text");
        Assert.hasText(deployment, "deployment must have text");
        Assert.hasText(job, "job must have text");
        Assert.hasText(name, "name must have text");

        this.id = id;
        this.deployment = deployment;
        this.job = job;
        this.name = name;
    }

    @Override
    public int compareTo(Member member) {
        return this.name.compareTo(member.getName());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Member member = (Member) o;

        return name == null ? member.name == null : name.equals(member.name);
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
     * Returns the ID of the {@link Member}
     *
     * @return the ID of the {@link Member}
     */
    public String getId() {
        return this.id;
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
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return String.format("[id: %s, deployment: %s, job: %s, name: %s]", this.id, this.deployment, this.job, this.name);
    }

}
