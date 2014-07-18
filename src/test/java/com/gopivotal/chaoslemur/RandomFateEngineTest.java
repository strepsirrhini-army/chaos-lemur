/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RandomFateEngineTest {

    private final MockEnvironment environment = new MockEnvironment();

    private final Random random = mock(Random.class);

    private final RandomFateEngine fateEngine = new RandomFateEngine(0.3f, this.environment, this.random);

    @Test
    public void killDefault() {
        Member member = new Member("test");

        when(this.random.nextFloat()).thenReturn(0.2f);
        assertTrue(fateEngine.shouldDie(member));

    }

    @Test
    public void saveDefault() {
        Member member = new Member("test");

        when(this.random.nextFloat()).thenReturn(0.5f);
        assertFalse(fateEngine.shouldDie(member));

    }

    @Test
    public void killNonDefault() {
        this.environment.setProperty("test.probability", "0.7");
        Member member = new Member("test");

        when(this.random.nextFloat()).thenReturn(0.4f);
        assertTrue(fateEngine.shouldDie(member));

    }

}