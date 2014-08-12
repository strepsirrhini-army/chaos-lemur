/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

import org.junit.Test;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertNotNull;

public final class MemberTest {

    private final Member member = new Member("test-group", "test-name");

    @Test
    public void test() {
        assertNotNull(this.member);
    }

    @Test
    public void getGroup() {
        assertTrue(this.member.getGroup().equals("test-group"));
    }

    @Test
    public void getName() {
        assertTrue(this.member.getName().equals("test-name"));
    }

    @Test
    public void toStringTest() {
        assertTrue(this.member.toString().equals("[group: test-group, name: test-name]"));
    }
}