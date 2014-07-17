/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.infrastructure;

import org.junit.Test;

import static org.junit.Assert.assertFalse;

public final class SimpleInfrastructureTest {

    private final SimpleInfrastructure infrastructure = new SimpleInfrastructure();

    @Test
    public void getMembers() {
        assertFalse(this.infrastructure.getMembers().isEmpty());
    }

}