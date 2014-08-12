/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package com.gopivotal.chaoslemur;

import com.gopivotal.chaoslemur.infrastructure.Infrastructure;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.*;

public final class DestroyerTest {

    private final FateEngine fateEngine = mock(FateEngine.class);

    private final Infrastructure infrastructure = mock(Infrastructure.class);

    private final DataDog dataDog = mock(DataDog.class);

    private final Destroyer destroyer = new Destroyer(dataDog, this.infrastructure, "0/11 * * * * *", this.fateEngine);

    private final Member member1 = new Member("test-group", "test-name-1");

    private final Member member2 = new Member("test-group", "test-name-2");

    private final Set<Member> members = new HashSet<>();

    @Before
         public void members() {
        this.members.add(this.member1);
        this.members.add(this.member2);
        when(this.infrastructure.getMembers()).thenReturn(this.members);
    }

    @Test
    public void destroy() throws DestructionException {
        when(this.fateEngine.shouldDie(this.member1)).thenReturn(true);
        when(this.fateEngine.shouldDie(this.member2)).thenReturn(false);

        this.destroyer.destroy();

        verify(this.infrastructure).destroy(this.member1);
        verify(this.infrastructure, times(0)).destroy(this.member2);
    }

    @Test
    public void destroyFail() throws DestructionException {
        when(this.fateEngine.shouldDie(this.member1)).thenReturn(true);
        doThrow(new DestructionException()).when(this.infrastructure).destroy(this.member1);

        this.destroyer.destroy();
    }

}