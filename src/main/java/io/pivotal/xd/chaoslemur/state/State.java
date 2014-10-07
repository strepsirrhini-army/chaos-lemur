/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur.state;

/**
 * Valid states for Chaos Lemur
 * <ul>
 *  <li>{@link #STARTED}</li>
 *  <li>{@link #STOPPED}</li>
 *  </ul>
 */
public enum State {

    /**
     * Chaos Lemur is active
     */
    STARTED,

    /**
     * Chaos Lemur is paused
     */
    STOPPED
}
