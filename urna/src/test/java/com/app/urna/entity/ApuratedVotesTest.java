package com.app.urna.entity;

import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ApuratedVotesTest {

    @Test
    public void testApuratedVotes() {
        ApuratedVotes apuratedVotes = new ApuratedVotes();
        apuratedVotes.setTotal(100);
        apuratedVotes.setMayors(Collections.singletonList(new Candidate()));
        apuratedVotes.setCouncilors(Collections.singletonList(new Candidate()));

        assertEquals(100, apuratedVotes.getTotal());
        assertEquals(1, apuratedVotes.getMayors().size());
        assertEquals(1, apuratedVotes.getCouncilors().size());
    }
}
