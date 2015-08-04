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

package io.pivotal.strepsirrhini.chaoslemur.infrastructure;


import io.pivotal.strepsirrhini.chaoslemur.Member;

import java.util.Set;

/**
 * An abstraction for interfacing with multiple infrastructures.
 */
public interface Infrastructure {

    /**
     * Returns a {@link Set} of all {@link Member}s
     *
     * @return a {@link Set} of all {@link Member}s
     */
    Set<Member> getMembers();

    /**
     * Destroys a {@link Member}
     *
     * @param member The member to destroy
     * @throws DestructionException
     */
    void destroy(Member member) throws DestructionException;

}
