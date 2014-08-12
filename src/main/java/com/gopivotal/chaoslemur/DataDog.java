/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

interface DataDog {
    void sendEvent(String title, String message);

}
