/*
 * Copyright 2015 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public final class PrecedenceTest {

    @Test(expected = IllegalStateException.class)
    public void onlyNullValues() {
        new Precedence<>().get();
    }

    @Test
    public void test() {
        new Precedence<String>()
                .candidate(() -> null)
                .candidate("test-string")
                .get(s -> {
                    assertEquals("test-string", s);
                    return "alternate-string";
                });
    }
}
