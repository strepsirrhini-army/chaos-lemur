/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.state;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
abstract class AbstractRestControllerStateProvider implements StateProvider {

    static final String STATUS_KEY = "status";

    protected abstract void set(State state);

    @RequestMapping(method = RequestMethod.POST, value = "/state")
    @ResponseStatus(HttpStatus.ACCEPTED)
    void changeState(@RequestBody Map<String, String> payload) {
        String value = payload.get(STATUS_KEY);

        if (value == null) {
            throw new IllegalArgumentException("Payload is missing key 'status'");
        }

        try {
            set(State.valueOf(value.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("'%s' is an illegal value for '%s'", value, STATUS_KEY), e);
        }
    }

    @RequestMapping(method = RequestMethod.GET, value = "/state")
    Map<String, State> reportState() {
        Map<String, State> message = new HashMap<>();
        message.put(STATUS_KEY, get());
        return message;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    void handleParsingException() {
    }

}
