package com.app.urna.service;

import com.app.urna.entity.ApuratedVotes;
import com.app.urna.entity.Candidate;
import com.app.urna.entity.Elector;
import com.app.urna.entity.Vote;
import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.entity.enums.StatusElector;
import com.app.urna.repository.CandidateRepository;
import com.app.urna.repository.ElectorRepository;
import com.app.urna.repository.VoteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class VoteServiceTest {

    @InjectMocks
    private VoteService voteService;

    @Mock
    private ElectorRepository electorRepository;

    @Mock
    private CandidateRepository candidateRepository;

    @Mock
    private VoteRepository voteRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void voteWithPendingElector() {
        Elector elector = new Elector();
        elector.setStatus(StatusElector.PENDENTE);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));

        Exception exception = assertThrows(Exception.class, () -> {
            voteService.vote(1L, 1L, 1L);
        });

        assertEquals("Usuário com cadastro pendente tentou votar. O usuário foi bloqueado!", exception.getMessage());
        assertEquals(StatusElector.BLOQUEADO, elector.getStatus());
        verify(electorRepository).save(elector);
    }

    @Test
    void voteWithNonEligibleElector() {
        Elector elector = new Elector();
        elector.setStatus(StatusElector.BLOQUEADO);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));

        Exception exception = assertThrows(Exception.class, () -> {
            voteService.vote(1L, 1L, 1L);
        });

        assertEquals("Eleitor inapto para votação", exception.getMessage());
    }

    @Test
    void voteWithNonExistentMayorCandidate() {
        Elector elector = new Elector();
        elector.setStatus(StatusElector.APTO);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            voteService.vote(1L, 1L, 1L);
        });

        assertEquals("Candidato a prefeito não encontrado", exception.getMessage());
    }

    @Test
    void voteWithNonMayorCandidate() {
        Elector elector = new Elector();
        elector.setStatus(StatusElector.APTO);
        Candidate candidate = new Candidate();
        candidate.setCandidateFunction(CandidateFunction.COUNCILOR);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(candidate));

        Exception exception = assertThrows(Exception.class, () -> {
            voteService.vote(1L, 1L, 1L);
        });

        assertEquals("O candidato escolhido para prefeito é um candidato a vereador. Refaça a requisição!", exception.getMessage());
    }

    @Test
    void voteWithNonExistentCouncilorCandidate() {
        Elector elector = new Elector();
        elector.setStatus(StatusElector.APTO);
        Candidate mayorCandidate = new Candidate();
        mayorCandidate.setCandidateFunction(CandidateFunction.MAYOR);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(mayorCandidate), Optional.empty()); // councilorCandidate não existe

        Exception exception = assertThrows(Exception.class, () -> {
            voteService.vote(1L, 1L, 2L);
        });

        assertEquals("Candidato a vereador não encontrado", exception.getMessage()); // Verifique a mensagem de exceção esperada
    }

    @Test
    void voteWithNonCouncilorCandidate() {
        Elector elector = new Elector();
        elector.setStatus(StatusElector.APTO);
        Candidate candidate = new Candidate();
        candidate.setCandidateFunction(CandidateFunction.MAYOR);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(candidate));

        Exception exception = assertThrows(Exception.class, () -> {
            voteService.vote(1L, 1L, 1L);
        });

        assertEquals("O candidato escolhido para vereador é um candidato a prefeito. Refaça a requisição!", exception.getMessage());
    }

    @Test
    void voteSuccess() throws Exception {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setStatus(StatusElector.APTO);

        Candidate mayorCandidate = new Candidate();
        mayorCandidate.setId(1L);
        mayorCandidate.setCandidateFunction(CandidateFunction.MAYOR);

        Candidate councilorCandidate = new Candidate();
        councilorCandidate.setId(2L);
        councilorCandidate.setCandidateFunction(CandidateFunction.COUNCILOR);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        when(candidateRepository.findById(anyLong())).thenReturn(Optional.of(mayorCandidate), Optional.of(councilorCandidate));

        String result = voteService.vote(1L, 1L, 2L);

        assertNotNull(result);
        assertEquals(StatusElector.VOTOU, elector.getStatus());
        verify(electorRepository).save(elector);
        verify(voteRepository).save(any(Vote.class));
    }


    @Test
    void performCountingSuccess() {
        Candidate mayor1 = new Candidate();
        mayor1.setId(1L);
        mayor1.setCandidateFunction(CandidateFunction.MAYOR);

        Candidate mayor2 = new Candidate();
        mayor2.setId(2L);
        mayor2.setCandidateFunction(CandidateFunction.MAYOR);

        Candidate councilor1 = new Candidate();
        councilor1.setId(3L);
        councilor1.setCandidateFunction(CandidateFunction.COUNCILOR);

        Candidate councilor2 = new Candidate();
        councilor2.setId(4L);
        councilor2.setCandidateFunction(CandidateFunction.COUNCILOR);

        Vote vote1 = new Vote();
        vote1.setMayor(mayor1);
        vote1.setCouncilor(councilor1);

        Vote vote2 = new Vote();
        vote2.setMayor(mayor1);
        vote2.setCouncilor(councilor2);

        when(voteRepository.findAll()).thenReturn(Arrays.asList(vote1, vote2));
        when(candidateRepository.findAll()).thenReturn(Arrays.asList(mayor1, mayor2, councilor1, councilor2));

        ApuratedVotes result = voteService.performCounting();

        assertNotNull(result);
        assertEquals(2, result.getTotal());
        assertEquals(2, result.getMayors().size());
        assertEquals(2, result.getCouncilors().size());
        assertEquals(2, result.getMayors().get(0).getApuratedVotes());
        assertEquals(0, result.getMayors().get(1).getApuratedVotes());
        assertEquals(1, result.getCouncilors().get(0).getApuratedVotes());
        assertEquals(1, result.getCouncilors().get(1).getApuratedVotes());
    }

}
