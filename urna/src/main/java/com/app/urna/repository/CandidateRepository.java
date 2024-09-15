package com.app.urna.repository;

import com.app.urna.entity.Candidate;
import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.entity.enums.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    boolean existsByCandidateNumber(Long candidateNumber);

    // Busca candidatos pelo status
    List<Candidate> findByStatus(Status status);

    // Busca candidatos pelo status e por função
    List<Candidate> findByStatusAndCandidateFunction(Status status, CandidateFunction function);

    // Busca candidatos por função
    List<Candidate> findByCandidateFunction(CandidateFunction function);
}
