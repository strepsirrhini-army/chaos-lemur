/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

public final class MemberTest {

    private final Member member = new Member("test-id", "test-name", "test-group");

    @Test
    public void test() {
        assertNotNull(this.member);
    }

    @Test
    public void getId() {
        assertTrue(this.member.getId().equals("test-id"));
    }

    @Test
    public void getName() {
        assertTrue(this.member.getName().equals("test-name"));
    }

    @Test
    public void getGroup() {
        assertTrue(this.member.getGroup().equals("test-group"));
    }

    @Test
    public void toStringTest() {
        assertTrue(this.member.toString().equals("[id: test-id, name: test-name, group: test-group]"));
    }
}
