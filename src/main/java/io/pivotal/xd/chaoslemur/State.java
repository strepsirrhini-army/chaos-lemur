/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import java.util.Map;

public enum State {

    DESTROYING("destroying"),
    PAUSED("paused"),
    RUNNING("running"),
    UNKNOWN("");

    private static final String STATUS_KEY = "status";

    private final String state;

    private State(String state) {
        this.state = state;
    }

    public static State parse(Map<String, String> payload) {
        String value = payload.get(STATUS_KEY);

        for (State candidate : values()) {
            if (candidate.state.equals(value)) {
                return candidate;
            }
        }
        return UNKNOWN;
    }

    @Override
    public String toString() {
        return this.state;
    }

}
