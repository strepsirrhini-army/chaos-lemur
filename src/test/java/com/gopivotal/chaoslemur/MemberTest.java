/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public final class MemberTest {

    private final Member member = new Member("group");

    @Test
    public void test() {
        assertNotNull(this.member);
    }

}