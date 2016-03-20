/*
 * Copyright 2014-2016 the original author or authors.
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

package io.pivotal.strepsirrhini.chaoslemur.state;

import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class RedisStateProviderTest {

    private static final String KEY = "state";

    private Jedis jedis = mock(Jedis.class);

    private JedisPool jedisPool = mock(JedisPool.class);

    private final RedisStateProvider redisStateProvider = new RedisStateProvider(this.jedisPool);

    @Test
    public void getDefault() {
        assertEquals(State.STARTED, this.redisStateProvider.get());
    }

    @Test
    public void getValue() {
        when(this.jedis.get(KEY)).thenReturn(State.STOPPED.toString());
        assertEquals(State.STOPPED, this.redisStateProvider.get());
    }

    @Test(expected = JedisConnectionException.class)
    public void jedisConnectionException() {
        try {
            when(this.jedis.get(KEY)).thenThrow(new JedisConnectionException("Jedis Connection Exception"));
            this.redisStateProvider.get();
        } finally {
            verify(this.jedisPool).returnBrokenResource(this.jedis);
            verify(this.jedisPool).returnResource(this.jedis);
        }
    }

    @Before
    public void jedisPool() {
        when(this.jedisPool.getResource()).thenReturn(this.jedis);
    }

    @Test
    public void set() {
        this.redisStateProvider.set(State.STOPPED);
        verify(this.jedis).set(KEY, State.STOPPED.toString());
    }

}
