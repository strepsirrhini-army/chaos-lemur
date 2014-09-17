/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import java.util.Map;

public enum State {

    DESTROYING,
    PAUSED,
    RUNNING;

    private static final String STATUS_KEY = "status";

    public static State parse(Map<String, String> payload) {
        String value = payload.get(STATUS_KEY);

        if (value == null) {
            throw new IllegalArgumentException("Payload is missing key 'status'");
        }

        try {
            return State.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    String.format("'%s' is an illegal value for key '%s'", value, STATUS_KEY), e);
        }
    }

}
