/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.xd.chaoslemur;

import io.pivotal.xd.chaoslemur.infrastructure.DestructionException;
import io.pivotal.xd.chaoslemur.infrastructure.Infrastructure;
import io.pivotal.xd.chaoslemur.reporter.Reporter;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public final class DestroyerTest {

    private final FateEngine fateEngine = mock(FateEngine.class);

    private final Infrastructure infrastructure = mock(Infrastructure.class);

    private final Reporter reporter = mock(Reporter.class);

    private final ExecutorService executor = mock(ExecutorService.class);

    private final Destroyer destroyer = new Destroyer(this.reporter, this.infrastructure, "0/11 * * * * *", this.fateEngine, this.executor);

    private final Member member1 = new Member("test-id-1", "test-name-1", "test-group");

    private final Member member2 = new Member("test-id-2", "test-name-2", "test-group");

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
