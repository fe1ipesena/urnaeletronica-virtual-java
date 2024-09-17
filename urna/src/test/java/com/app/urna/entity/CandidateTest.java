package com.app.urna.entity;

import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.entity.enums.Status;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;



import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CandidateTest {

    private final Validator validator;

    public CandidateTest() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidCandidate() {
        Candidate candidate = new Candidate();
        candidate.setName("Bolsonaro Lula da Silva");
        candidate.setCpf("877.238.950-89");
        candidate.setCandidateNumber(123L);
        candidate.setCandidateFunction(CandidateFunction.MAYOR);
        candidate.setStatus(Status.ATIVO);

        var violations = validator.validate(candidate);
        assertTrue(violations.isEmpty());
    }

    @Test
    public void testInvalidCandidate() {
        Candidate candidate = new Candidate();
        candidate.setName("Jana");
        candidate.setCpf("123");
        candidate.setCandidateNumber(null);
        candidate.setCandidateFunction(null);
        candidate.setStatus(null);

        var violations = validator.validate(candidate);
        assertFalse(violations.isEmpty());
    }
}
