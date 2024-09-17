package com.app.urna.entity;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class VoteTest {

    @InjectMocks
    private Vote vote;

    public VoteTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testPrePersist() {
        vote.setReceipt(null);
        vote.setMoment(null);

        vote.prePersist();

        assertNotNull(vote.getReceipt());
        assertNotNull(vote.getMoment());
    }

    @Test
    public void testPrePersistWithExistingValues() {
        String existingReceipt = "existing-receipt";
        Date existingDate = new Date();
        vote.setReceipt(existingReceipt);
        vote.setMoment(existingDate);

        vote.prePersist();

        assertEquals(existingReceipt, vote.getReceipt());
        assertEquals(existingDate, vote.getMoment());
    }
}
