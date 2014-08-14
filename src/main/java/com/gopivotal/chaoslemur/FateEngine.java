/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

interface FateEngine {

    /**
     * Determines whether a {@link Member} should live or die
     *
     * @param member The {@link Member} to evaluate
     * @return Whether a member should live or die
     */
    Boolean shouldDie(Member member);

}
