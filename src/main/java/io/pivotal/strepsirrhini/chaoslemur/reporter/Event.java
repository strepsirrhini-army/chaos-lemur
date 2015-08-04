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

package io.pivotal.strepsirrhini.chaoslemur.reporter;

import io.pivotal.strepsirrhini.chaoslemur.Member;

import java.util.List;
import java.util.UUID;

/**
 * Encapsulates information about an event that should be reported
 */
public final class Event {

    private final UUID identifier;

    private final List<Member> members;

    /**
     * Create a new instance
     *
     * @param identifier the identifier of the event
     * @param members    the list of members destroyed during this event
     */
    public Event(UUID identifier, List<Member> members) {
        this.identifier = identifier;
        this.members = members;
    }

    /**
     * Returns the identifier of the event
     *
     * @return the identifier of the event
     */
    public UUID getIdentifier() {
        return this.identifier;
    }

    /**
     * Returns the list of members destroyed during this event
     *
     * @return the list of members destroyed during this event
     */
    public List<Member> getMembers() {
        return this.members;
    }
}
