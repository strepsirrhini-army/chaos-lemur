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

package io.pivotal.xd.chaoslemur.reporter;

/**
 * An abstraction for interfacing with Reporters in different ways
 */
public interface Reporter {
    /**
     * Sends an Event to an Event Recorder
     *
     * @param title   The title of the Event
     * @param message The message of the Event
     */
    void sendEvent(String title, String message);

}
