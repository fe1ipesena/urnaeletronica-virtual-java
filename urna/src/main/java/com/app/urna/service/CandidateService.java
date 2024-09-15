package com.app.urna.service;

import com.app.urna.entity.Candidate;
import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.entity.enums.Status;
import com.app.urna.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CandidateService {

    @Autowired
    private CandidateRepository candidateRepository;

    @Transactional
    public Candidate save(Candidate candidate) {
        if (candidateRepository.existsByCandidateNumber(candidate.getCandidateNumber())) {
            throw new IllegalArgumentException("O número do candidato já existe. Escolha outro número.");
        }
        candidate.setStatus(Status.ATIVO);
        return candidateRepository.save(candidate);
    }

    @Transactional
    public Candidate update(Long id, Candidate candidateDetails) {
        Candidate existingCandidate = findById(id);

        if (existingCandidate == null) {
            throw new EntityNotFoundException("Candidato não encontrado");
        }

        existingCandidate.setName(candidateDetails.getName());
        existingCandidate.setCpf(candidateDetails.getCpf());
        existingCandidate.setCandidateNumber(candidateDetails.getCandidateNumber());
        existingCandidate.setCandidateFunction(candidateDetails.getCandidateFunction());

        if (existingCandidate.getStatus() == Status.INATIVO) {
            candidateDetails.setStatus(Status.INATIVO);
        } else {
            candidateDetails.setStatus(Status.ATIVO);
        }

        return candidateRepository.save(existingCandidate);
    }

    @Transactional
    public String delete(Long id) {
        Candidate candidate = findById(id);

        if (candidate == null) {
            throw new EntityNotFoundException("Candidato não encontrado");
        }

        if (candidate.getStatus() == Status.ATIVO) {
            candidate.setStatus(Status.INATIVO);
            candidateRepository.save(candidate);
            return "Candidato inativado com sucesso";
        } else {
            throw new RuntimeException("Não é possível inativar um candidato que já está INATIVO");
        }
    }

    public Candidate findById(Long id) {
        Optional<Candidate> candidate = candidateRepository.findById(id);
        return candidate.orElse(null);
    }

    public List<Candidate> findAll() {
        return candidateRepository.findByStatus(Status.ATIVO);
    }

    public List<Candidate> findActiveCandidatesByPosition(CandidateFunction function) {
        return candidateRepository.findByStatusAndCandidateFunction(Status.ATIVO, function);
    }

    public List<Candidate> findActiveCandidatesForMayor() {
        return candidateRepository.findByCandidateFunction(CandidateFunction.MAYOR);
    }

    public List<Candidate> findActiveCandidatesForCouncilor() {
        return candidateRepository.findByCandidateFunction(CandidateFunction.COUNCILOR);
    }
}
