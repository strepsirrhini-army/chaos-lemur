/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import org.junit.Test;
import org.springframework.mock.env.MockEnvironment;

import java.util.Random;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class RandomFateEngineTest {

    private final String[] blacklist = new String[]{"test-deployment-1", "test-job-1"};

    private final MockEnvironment environment = new MockEnvironment();

    private final Member member = new Member("test-id", "test-deployment", "test-job", "test-name");

    private final Random random = mock(Random.class);

    private final RandomFateEngine fateEngine = new RandomFateEngine(this.blacklist, 0.5f, this.environment,
            this.random);

    @Test
    public void blacklist() {
        when(this.random.nextFloat()).thenReturn(0.0f);

        assertTrue(this.fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job", "test-name-1")));

        assertFalse(this.fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job-1", "test-name-1")));
        assertFalse(this.fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job", "test-name-1")));
    }

    @Test
    public void jobPrecedence() {
        when(this.random.nextFloat()).thenReturn(0.5f);
        this.environment.setProperty("test-job.probability", "1.0");

        assertTrue(this.fateEngine.shouldDie(this.member));
    }

    @Test
    public void deploymentPrecedence() {
        when(this.random.nextFloat()).thenReturn(0.5f);
        this.environment.setProperty("test-deployment.probability", "1.0");

        assertTrue(this.fateEngine.shouldDie(this.member));
    }

    @Test
    public void defaultPrecedence() {
        when(this.random.nextFloat()).thenReturn(0.4f);

        assertTrue(this.fateEngine.shouldDie(this.member));
    }

}
