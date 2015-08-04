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

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
abstract class AbstractRestControllerStateProvider implements StateProvider {

    private static final String STATUS_KEY = "status";

    protected abstract void set(State state);

    @RequestMapping(method = RequestMethod.POST, value = "/state", consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> changeState(@RequestBody Map<String, String> payload) {
        String value = payload.get(STATUS_KEY);

        if (value == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            set(State.valueOf(value.toUpperCase()));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

    }

    @RequestMapping(method = RequestMethod.GET, value = "/state", produces = MediaType.APPLICATION_JSON_VALUE)
    Map<String, State> reportState() {
        Map<String, State> message = new HashMap<>();
        message.put(STATUS_KEY, get());
        return message;
    }

}
