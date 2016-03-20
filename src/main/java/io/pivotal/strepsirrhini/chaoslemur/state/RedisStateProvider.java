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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.function.Function;

final class RedisStateProvider extends AbstractRestControllerStateProvider {

    private static final String KEY = "state";

    private final JedisTemplate jedisTemplate;

    private final Object monitor = new Object();

    @Autowired
    RedisStateProvider(JedisPool jedisPool) {
        this.jedisTemplate = new JedisTemplate(jedisPool);
    }

    @Override
    public State get() {
        synchronized (this.monitor) {
            return this.jedisTemplate.execute(jedis -> {
                String candidate = jedis.get(KEY);
                if (candidate == null) {
                    return State.STARTED;
                }

                return State.valueOf(candidate);
            });
        }
    }

    @Override
    protected void set(State state) {
        synchronized (this.monitor) {
            this.jedisTemplate.execute(jedis -> jedis.set(KEY, state.toString()));
        }
    }

    private static final class JedisTemplate {

        private final Logger logger = LoggerFactory.getLogger(this.getClass());

        private final JedisPool jedisPool;

        private JedisTemplate(JedisPool jedisPool) {
            this.jedisPool = jedisPool;
        }

        private <R> R execute(Function<Jedis, R> function) {
            Jedis jedis = null;
            try {
                jedis = this.jedisPool.getResource();
                return function.apply(jedis);
            } catch (JedisConnectionException e) {
                returnBrokenResourceQuietly(jedis);
                throw e;
            } finally {
                returnResourceQuietly(jedis);
            }
        }

        private void returnBrokenResourceQuietly(Jedis jedis) {
            if (jedis != null) {
                try {
                    this.jedisPool.returnBrokenResource(jedis);
                } catch (RuntimeException e) {
                    this.logger.warn("Failed to return broken Jedis resource", e);
                }
            }
        }

        private void returnResourceQuietly(Jedis jedis) {
            if (jedis != null) {
                try {
                    this.jedisPool.returnResource(jedis);
                } catch (RuntimeException e) {
                    this.logger.warn("Failed to return Jedis resource", e);
                }
            }
        }
    }

}
