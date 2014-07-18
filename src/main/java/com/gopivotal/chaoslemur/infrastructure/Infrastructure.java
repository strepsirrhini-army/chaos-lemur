/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur.infrastructure;


import com.gopivotal.chaoslemur.DestructionException;
import com.gopivotal.chaoslemur.Member;

import java.util.Set;

/**
 * An abstraction for interfacing with multiple infrastructures.
 */
public interface Infrastructure {

    /**
     * Returns a {@link Set} of all {@link Member}s
     *
     * @return a {@link Set} of all {@link Member}s
     */
    Set<Member> getMembers();

    /**
     * Destroys a {@link Member}
     *
     * @param member The member to destroy
     * @throws DestructionException
     */
    void destroy(Member member) throws DestructionException;
}