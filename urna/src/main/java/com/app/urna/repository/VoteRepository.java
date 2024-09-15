package com.app.urna.repository;

import com.app.urna.entity.Candidate;
import com.app.urna.entity.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    // Conta o número de votos recebidos por um candidato a prefeito
    Long countByMayorId(Long mayorId);

    // Conta o número de votos recebidos por um candidato a vereador
    Long countByCouncilorId(Long councilorId);
}
