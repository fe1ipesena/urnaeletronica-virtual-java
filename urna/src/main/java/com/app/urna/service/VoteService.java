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
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class VoteService {

    @Autowired
    private ElectorRepository electorRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Transactional
    public String vote(Long electorId, Long mayorId, Long councilorId) throws Exception {
        Elector elector = electorRepository.findById(electorId)
                .orElseThrow(() -> new Exception("Eleitor não encontrado"));

        // Se o status do eleitor for PENDENTE, da block nele
        if (elector.getStatus() == StatusElector.PENDENTE) {
            elector.setStatus(StatusElector.BLOQUEADO);
            electorRepository.save(elector);
            throw new Exception("Usuário com cadastro pendente tentou votar. O usuário foi bloqueado!");
        }

        // Se ñ for APTO
        if (elector.getStatus() != StatusElector.APTO) {
            throw new Exception("Eleitor inapto para votação");
        }

        Vote vote = new Vote();

        // Verifique se os candidatos existem e são do tipo correto
        Candidate mayorCandidate = candidateRepository.findById(mayorId)
                .orElseThrow(() -> new EntityNotFoundException("Candidato a prefeito não encontrado"));
        if (mayorCandidate.getCandidateFunction() != CandidateFunction.MAYOR) {
            throw new Exception("O candidato escolhido para prefeito é um candidato a vereador. Refaça a requisição!");
        }

        Candidate councilorCandidate = candidateRepository.findById(councilorId)
                .orElseThrow(() -> new EntityNotFoundException("Candidato a vereador não encontrado"));
        if (councilorCandidate.getCandidateFunction() != CandidateFunction.COUNCILOR) {
            throw new Exception("O candidato escolhido para vereador é um candidato a prefeito. Refaça a requisição!");
        }
        vote.setMayor(mayorCandidate);
        vote.setCouncilor(councilorCandidate);

        // Att o voto e o stts do eleitor
        vote.setMoment(new Date());
        String hash = UUID.randomUUID().toString();
        vote.setReceipt(hash);

        elector.setStatus(StatusElector.VOTOU);
        electorRepository.save(elector);

        voteRepository.save(vote);

        return hash;
    }


    @Transactional
    public ApuratedVotes performCounting() {
        List<Vote> votes = voteRepository.findAll();

        //Contadores para prefeitos e vereadores
        Map<Long, Integer> mayorVoteCounts = new HashMap<>();
        Map<Long, Integer> councilorVoteCounts = new HashMap<>();

        // Contabiliza os votos
        for (Vote vote : votes) {
            if (vote.getMayor() != null && vote.getMayor().getId() != null) {
                Long mayorId = vote.getMayor().getId();
                mayorVoteCounts.put(mayorId, mayorVoteCounts.getOrDefault(mayorId, 0) + 1);
            }
            if (vote.getCouncilor() != null && vote.getCouncilor().getId() != null) {
                Long councilorId = vote.getCouncilor().getId();
                councilorVoteCounts.put(councilorId, councilorVoteCounts.getOrDefault(councilorId, 0) + 1);
            }
        }

        List<Candidate> allCandidates = candidateRepository.findAll();

        // Cria lista pra prefeito e vereador
        List<Candidate> mayors = allCandidates.stream()
                .filter(candidate -> candidate.getCandidateFunction() == CandidateFunction.MAYOR)
                .peek(candidate -> candidate.setApuratedVotes(mayorVoteCounts.getOrDefault(candidate.getId(), 0)))
                .collect(Collectors.toList());

        List<Candidate> councilors = allCandidates.stream()
                .filter(candidate -> candidate.getCandidateFunction() == CandidateFunction.COUNCILOR)
                .peek(candidate -> candidate.setApuratedVotes(councilorVoteCounts.getOrDefault(candidate.getId(), 0)))
                .collect(Collectors.toList());

        // Ordena pelos votos
        mayors.sort(Comparator.comparingInt(Candidate::getApuratedVotes).reversed());
        councilors.sort(Comparator.comparingInt(Candidate::getApuratedVotes).reversed());

        // Crie o objeto ApuratedVotes com os resultados
        ApuratedVotes apuratedVotes = new ApuratedVotes();
        apuratedVotes.setTotal(votes.size());
        apuratedVotes.setMayors(mayors);
        apuratedVotes.setCouncilors(councilors);

        return apuratedVotes;
    }
}


