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

package io.pivotal.strepsirrhini.chaoslemur.state;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

@Configuration
class StateConfiguration {

    @Bean
    @ConditionalOnProperty("vcap.services.chaos-lemur-persistence.credentials.hostname")
    JedisPool jedisPool(@Value("${vcap.services.chaos-lemur-persistence.credentials.hostname}") String hostname,
                        @Value("${vcap.services.chaos-lemur-persistence.credentials.port}") Integer port,
                        @Value("${vcap.services.chaos-lemur-persistence.credentials.password}") String password) {
        return new JedisPool(new JedisPoolConfig(), hostname, port, Protocol.DEFAULT_TIMEOUT, password);
    }

    @Bean
    @ConditionalOnBean(JedisPool.class)
    StateProvider redisStateProvider(JedisPool jedisPool) {
        return new RedisStateProvider(jedisPool);
    }

    @Bean
    @ConditionalOnMissingBean(StateProvider.class)
    StateProvider simpleStateProvider() {
        return new SimpleStateProvider();
    }

}
