/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.infrastructure;

import com.gopivotal.chaoslemur.Member;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public final class SimpleInfrastructureTest {

    private final Member member = new Member("test-id", "test-name", "test-group");

    private final SimpleInfrastructure infrastructure = new SimpleInfrastructure();

    @Test
    public void getMembers() {
        assertFalse(this.infrastructure.getMembers().isEmpty());
    }

    @Test
    public void destroy() throws DestructionException {
        this.infrastructure.destroy(this.member);
    }

}