package com.app.urna.repository;

import com.app.urna.entity.Candidate;
import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.entity.enums.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class CandidateRepositoryTest {

    @Autowired
    private CandidateRepository candidateRepository;

    @Test
    public void testExistsByCandidateNumber() {
        Candidate candidate = new Candidate();
        candidate.setCandidateNumber(123L);
        candidate.setCpf("628.245.090-07");
        candidate.setCandidateFunction(CandidateFunction.MAYOR);
        candidate.setName("Candidato Teste");
        candidateRepository.save(candidate);

        boolean exists = candidateRepository.existsByCandidateNumber(123L);
        assertThat(exists).isTrue();
    }

    @Test
    public void testFindByStatus() {
        Candidate candidate = new Candidate();
        candidate.setStatus(Status.ATIVO);
        candidate.setCandidateNumber(456L);
        candidate.setCpf("628.245.090-07");
        candidate.setCandidateFunction(CandidateFunction.COUNCILOR);
        candidate.setName("Candidato Teste");
        candidateRepository.save(candidate);

        List<Candidate> candidates = candidateRepository.findByStatus(Status.ATIVO);
        assertThat(candidates).isNotEmpty();
    }

    @Test
    public void testFindByStatusAndCandidateFunction() {
        Candidate candidate = new Candidate();
        candidate.setStatus(Status.ATIVO);
        candidate.setCandidateFunction(CandidateFunction.MAYOR);
        candidate.setCandidateNumber(789L);
        candidate.setCpf("628.245.090-07");
        candidate.setName("Candidato Teste");
        candidateRepository.save(candidate);

        List<Candidate> candidates = candidateRepository.findByStatusAndCandidateFunction(Status.ATIVO, CandidateFunction.MAYOR);
        assertThat(candidates).isNotEmpty();
    }

    @Test
    public void testFindByCandidateFunction() {
        Candidate candidate = new Candidate();
        candidate.setCandidateFunction(CandidateFunction.COUNCILOR);
        candidate.setCandidateNumber(101L);
        candidate.setCpf("628.245.090-07");
        candidate.setName("Candidato Teste");
        candidate.setStatus(Status.ATIVO);
        candidateRepository.save(candidate);

        List<Candidate> candidates = candidateRepository.findByCandidateFunction(CandidateFunction.COUNCILOR);
        assertThat(candidates).isNotEmpty();
    }
}
