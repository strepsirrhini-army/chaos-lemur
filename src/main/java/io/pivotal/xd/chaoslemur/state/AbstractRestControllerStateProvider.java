/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.state;

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
