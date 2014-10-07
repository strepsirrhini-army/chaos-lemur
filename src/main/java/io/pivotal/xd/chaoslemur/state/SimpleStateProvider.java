/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.state;

final class SimpleStateProvider extends AbstractRestControllerStateProvider {

    private final Object monitor = new Object();

    private volatile State state = State.STARTED;

    @Override
    public State get() {
        synchronized (this.monitor) {
            return this.state;
        }
    }

    @Override
    protected void set(State state) {
        synchronized (this.monitor) {
            this.state = state;
        }
    }

}
