/*
 * Copyright 2014-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

    private final String[] whitelist = new String[0];

    private final RandomFateEngine fateEngine = new RandomFateEngine(this.blacklist, 0.5f, this.environment,
            this.random, this.whitelist);

    @Test
    public void neitherWhiteListNorBlacklistSpecified() {
        when(this.random.nextFloat()).thenReturn(0.0f);
        RandomFateEngine fateEngine = new RandomFateEngine(new String[0], 0.5f, this.environment, this.random,
                new String[0]);

        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job", "test-name-1")));
        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job-1", "test-name-1")));
        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job", "test-name-1")));
    }

    @Test
    public void blacklistOnly() {
        when(this.random.nextFloat()).thenReturn(0.0f);

        assertTrue(this.fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job", "test-name-1")));

        assertFalse(this.fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job-1", "test-name-1")));
        assertFalse(this.fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job", "test-name-1")));
    }

    @Test
    public void whitelistOnlyDeployments() {
        when(this.random.nextFloat()).thenReturn(0.0f);

        String[] whitelist = new String[]{"test-deployment"};
        RandomFateEngine fateEngine = new RandomFateEngine(new String[0], 0.5f, this.environment, this.random,
                whitelist);

        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job", "test-name-1")));
        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job-1", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job", "test-name-1")));
    }

    @Test
    public void whitelistOnlyJobs() {
        when(this.random.nextFloat()).thenReturn(0.0f);

        String[] whitelist = new String[]{"test-job-1"};
        RandomFateEngine fateEngine = new RandomFateEngine(new String[0], 0.5f, this.environment, this.random,
                whitelist);

        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job", "test-name-1")));
        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment", "test-job-1", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job", "test-name-1")));
    }

    @Test
    public void whitelistAndBlacklistCombinedDeployments() {
        when(this.random.nextFloat()).thenReturn(0.0f);

        String[] whitelist = new String[]{"test-deployment-1"};
        String[] blacklist = new String[]{"test-job-3", "test-job-4"};
        RandomFateEngine fateEngine = new RandomFateEngine(blacklist, 0.5f, this.environment, this.random, whitelist);

        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job-1", "test-name-1")));
        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job-2", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job-3", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job-4", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-2", "test-job-5", "test-name-1")));
    }

    @Test
    public void whitelistAndBlacklistCombinedJobs() {
        when(this.random.nextFloat()).thenReturn(0.0f);

        String[] whitelist = new String[]{"test-job-1"};
        String[] blacklist = new String[]{"test-job-3", "test-job-4"};
        RandomFateEngine fateEngine = new RandomFateEngine(blacklist, 0.5f, this.environment, this.random, whitelist);

        assertTrue(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job-1", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job-2", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job-3", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-1", "test-job-4", "test-name-1")));
        assertFalse(fateEngine.shouldDie(new Member("test-id-1", "test-deployment-2", "test-job-5", "test-name-1")));
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
