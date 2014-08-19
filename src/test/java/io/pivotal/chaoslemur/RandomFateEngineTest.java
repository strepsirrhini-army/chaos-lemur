/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.chaoslemur;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RandomFateEngineTest {

    private final String[] blacklist = new String[]{"test-group-1", "test-group-2"};

    private final MockEnvironment environment = new MockEnvironment();

    private final Random random = mock(Random.class);

    private final RandomFateEngine fateEngine = new RandomFateEngine(this.blacklist, 0.3f, this.environment,
            this.random);

    @Test
    public void killDefault() {
        Member member = new Member("test-id", "test-name", "test-group");

        when(this.random.nextFloat()).thenReturn(0.2f);
        assertTrue(fateEngine.shouldDie(member));

    }

    @Test
    public void saveDefault() {
        Member member = new Member("test-id", "test-name", "test-group");

        when(this.random.nextFloat()).thenReturn(0.5f);
        assertFalse(fateEngine.shouldDie(member));

    }

    @Test
    public void killNonDefault() {
        this.environment.setProperty("test-group.probability", "0.7");
        Member member = new Member("test-id", "test-name", "test-group");

        when(this.random.nextFloat()).thenReturn(0.4f);
        assertTrue(fateEngine.shouldDie(member));

    }

    @Test
    public void blacklist() {
        Member member = new Member("test-id-1", "test-name-1", "test-group-1");

        when(this.random.nextFloat()).thenReturn(0.0f);
        assertFalse(fateEngine.shouldDie(member));

    }

}