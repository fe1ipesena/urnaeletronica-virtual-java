package com.app.urna.service;

import com.app.urna.entity.Candidate;
import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.entity.enums.Status;
import com.app.urna.repository.CandidateRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CandidateServiceTest {

    @InjectMocks
    private CandidateService candidateService;

    @Mock
    private CandidateRepository candidateRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveCandidateSuccess() {
        Candidate candidate = new Candidate();
        candidate.setCandidateNumber(1L);
        candidate.setStatus(Status.ATIVO);

        when(candidateRepository.existsByCandidateNumber(anyLong())).thenReturn(false);
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        Candidate result = candidateService.save(candidate);

        assertEquals(candidate, result);
        verify(candidateRepository).save(candidate);
    }

    @Test
    void saveCandidateFailure() {
        Candidate candidate = new Candidate();
        candidate.setCandidateNumber(1L);

        when(candidateRepository.existsByCandidateNumber(anyLong())).thenReturn(true);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            candidateService.save(candidate);
        });

        assertEquals("O número do candidato já existe. Escolha outro número.", exception.getMessage());
    }

    @Test
    void deleteCandidateSuccess() {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setStatus(Status.ATIVO);

        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(candidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(candidate);

        String result = candidateService.delete(1L);

        assertEquals("Candidato inativado com sucesso", result);
        assertEquals(Status.INATIVO, candidate.getStatus());
        verify(candidateRepository).save(candidate);
    }

    @Test
    void deleteCandidateNotFound() {
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            candidateService.delete(1L);
        });

        assertEquals("Candidato não encontrado", exception.getMessage());
    }

    @Test
    void updateCandidateSuccess() {
        Candidate existingCandidate = new Candidate();
        existingCandidate.setId(1L);
        existingCandidate.setStatus(Status.ATIVO);

        Candidate candidateDetails = new Candidate();
        candidateDetails.setName("Updated Name");
        candidateDetails.setCpf("Updated CPF");
        candidateDetails.setCandidateNumber(Long.valueOf("2"));
        candidateDetails.setCandidateFunction(CandidateFunction.COUNCILOR);

        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(existingCandidate));
        when(candidateRepository.save(any(Candidate.class))).thenReturn(existingCandidate);

        Candidate result = candidateService.update(1L, candidateDetails);

        assertEquals(existingCandidate, result);
        verify(candidateRepository).save(existingCandidate);
    }

    @Test
    void findAllSuccess() {
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate());

        when(candidateRepository.findByStatus(Status.ATIVO)).thenReturn(candidates);
        List<Candidate> result = candidateService.findAll();

        assertEquals(candidates, result);
        verify(candidateRepository).findByStatus(Status.ATIVO);
    }

    @Test
    void findAllActivesSuccess() {
        List<Candidate> activeCandidates = new ArrayList<>();
        Candidate active = new Candidate();
        active.setStatus(Status.ATIVO);
        activeCandidates.add(active);

        when(candidateRepository.findByStatus(Status.ATIVO)).thenReturn(activeCandidates);
        List<Candidate> result = candidateService.findAll();

        assertEquals(1, result.size(), "O número de candidatos ativos está incorreto.");
        assertTrue(result.contains(active), "O candidato ativo não foi encontrado na lista.");
        verify(candidateRepository).findByStatus(Status.ATIVO);
    }

    @Test
    void findActiveCandidatesByPositionSuccess() {
        Candidate candidate = new Candidate();
        candidate.setCandidateFunction(CandidateFunction.COUNCILOR);
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(candidate);

        when(candidateRepository.findByStatusAndCandidateFunction(Status.ATIVO, CandidateFunction.COUNCILOR))
                .thenReturn(candidates);
        List<Candidate> result = candidateService.findActiveCandidatesByPosition(CandidateFunction.COUNCILOR);

        assertEquals(1, result.size(), "O número de candidatos ativos para a função está incorreto.");
        assertTrue(result.contains(candidate), "O candidato ativo para a função não foi encontrado na lista.");
        verify(candidateRepository).findByStatusAndCandidateFunction(Status.ATIVO, CandidateFunction.COUNCILOR);
    }

    @Test
    void findActiveCandidatesByPositionFailure() {
        when(candidateRepository.findByStatusAndCandidateFunction(Status.ATIVO, CandidateFunction.MAYOR))
                .thenReturn(new ArrayList<>());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            candidateService.findActiveCandidatesByPosition(CandidateFunction.MAYOR);
        });

        assertEquals("Nenhum candidato ativo encontrado para a função: MAYOR", exception.getMessage());
    }

    @Test
    void findActiveCandidatesForMayor() {
        Candidate candidate = new Candidate();
        candidate.setCandidateFunction(CandidateFunction.MAYOR);
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(candidate);

        when(candidateRepository.findByCandidateFunction(CandidateFunction.MAYOR)).thenReturn(candidates);
        List<Candidate> result = candidateService.findActiveCandidatesForMayor();

        assertEquals(1, result.size(), "O número de candidatos ativos para prefeito está incorreto.");
        assertTrue(result.contains(candidate), "O candidato ativo para prefeito não foi encontrado na lista.");
        verify(candidateRepository).findByCandidateFunction(CandidateFunction.MAYOR);
    }

    @Test
    void findActiveCandidatesForCouncilor() {
        Candidate candidate = new Candidate();
        candidate.setCandidateFunction(CandidateFunction.COUNCILOR);
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(candidate);

        when(candidateRepository.findByCandidateFunction(CandidateFunction.COUNCILOR)).thenReturn(candidates);
        List<Candidate> result = candidateService.findActiveCandidatesForCouncilor();

        assertEquals(1, result.size(), "O número de candidatos ativos para vereador está incorreto.");
        assertTrue(result.contains(candidate), "O candidato ativo para vereador não foi encontrado na lista.");
        verify(candidateRepository).findByCandidateFunction(CandidateFunction.COUNCILOR);
    }
}
