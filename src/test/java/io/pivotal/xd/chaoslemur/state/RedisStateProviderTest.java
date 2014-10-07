/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.state;

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

    private JedisPool jedisPool = mock(JedisPool.class);

    private Jedis jedis = mock(Jedis.class);

    private final RedisStateProvider redisStateProvider = new RedisStateProvider(this.jedisPool);

    @Before
    public void jedisPool() {
        when(this.jedisPool.getResource()).thenReturn(this.jedis);
    }

    @Test
    public void getDefault() {
        assertEquals(State.STARTED, this.redisStateProvider.get());
    }

    @Test
    public void getValue() {
        when(this.jedis.get(KEY)).thenReturn(State.STOPPED.toString());
        assertEquals(State.STOPPED, this.redisStateProvider.get());
    }

    @Test
    public void set() {
        this.redisStateProvider.set(State.STOPPED);
        verify(this.jedis).set(KEY, State.STOPPED.toString());
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

}