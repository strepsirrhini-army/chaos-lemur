/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.state;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class SimpleStateProviderTest {

    private final SimpleStateProvider simpleStateProvider = new SimpleStateProvider();

    @Test
    public void get() { assertEquals(State.STARTED, simpleStateProvider.get()); }

    @Test
    public void set() { simpleStateProvider.set(State.STOPPED); }
}