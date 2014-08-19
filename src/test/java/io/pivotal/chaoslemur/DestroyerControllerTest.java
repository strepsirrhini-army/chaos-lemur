/*
 * Copyright 2014 Pivotal Software, Inc. All Rights Reserved.
 */

package io.pivotal.chaoslemur;

import io.pivotal.chaoslemur.datadog.DataDog;
import io.pivotal.chaoslemur.infrastructure.Infrastructure;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public final class DestroyerControllerTest {

    private final DataDog dataDog = mock(DataDog.class);

    private final Infrastructure infrastructure = mock(Infrastructure.class);

    private final FateEngine fateEngine = mock(FateEngine.class);

    private final MockMvc mockMvc = standaloneSetup(new Destroyer(this.dataDog, this.infrastructure, "",
            this.fateEngine)).build();

    @Test
    public void destroy() throws Exception {
        Member member = new Member("test-id", "test-name", "test-group");
        Set<Member> members = new HashSet<>();
        members.add(member);

        when(this.infrastructure.getMembers()).thenReturn(members);
        when(fateEngine.shouldDie(member)).thenReturn(true);

        this.mockMvc.perform(post("/destroy")).andExpect(status().isOk());

        verify(this.infrastructure).destroy(member);
    }
}
