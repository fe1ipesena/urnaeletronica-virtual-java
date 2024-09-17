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

    List<Candidate> findByStatus(Status status);

    List<Candidate> findByStatusAndCandidateFunction(Status ativo, CandidateFunction function);

    List<Candidate> findByCandidateFunction(CandidateFunction function);
}
