/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.infrastructure;

import io.pivotal.xd.chaoslemur.Member;
import org.junit.Test;

import static org.junit.Assert.assertFalse;

public final class SimpleInfrastructureTest {

    private final Member member = new Member("test-id", "test-deployment", "test-job", "test-name");

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
