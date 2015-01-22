/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public final class MemberTest {

    private final Member member = new Member("test-id", "test-deployment", "test-job", "test-name");

    private final Member compare = new Member("compare-id", "compare-deployment", "compare-job", "compare-name");

    @Test
    public void test() {
        assertEquals("test-id", this.member.getId());
        assertEquals("test-deployment", this.member.getDeployment());
        assertEquals("test-job", this.member.getJob());
        assertEquals("test-name", this.member.getName());

        assertEquals("[id: test-id, deployment: test-deployment, job: test-job, name: test-name]",
                this.member.toString());
    }

    @Test
    public void compareTo() {
        assertTrue(this.member.compareTo(compare) > 0);
    }

}
