package com.app.urna.service;

import com.app.urna.entity.Elector;
import com.app.urna.repository.ElectorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;

@Service
public class ElectorService {

    @Autowired
    private ElectorRepository electorRepository;

    @Transactional
    public Elector save(Elector elector) {
        if (elector.getCpf() == null || elector.getEmail() == null) { // Dfine o stts PENDENTE se CPF ou e-mail estiver faltando
            elector.setStatus(Elector.Status.PENDENTE);
        } else {
            elector.setStatus(Elector.Status.APTO); // Dfine o stts APTO se não tiver pendencias
        }
        return electorRepository.save(elector);
    }

    @Transactional
    public Elector update(Long id, Elector electorDetails) {
        Elector existingElector = findById(id);

        if (existingElector == null) {
            throw new EntityNotFoundException("Eleitor não encontrado");
        }

        // Se o eleitor já está INATIVO, mantem o stts como INATIVO
        if (existingElector.getStatus() == Elector.Status.INATIVO) {
            existingElector.setName(electorDetails.getName());
            existingElector.setCpf(electorDetails.getCpf());
            existingElector.setJob(electorDetails.getJob());
            existingElector.setMobilephone(electorDetails.getMobilephone());
            existingElector.setLandlinephone(electorDetails.getLandlinephone());
            existingElector.setEmail(electorDetails.getEmail());
            // Mantem o stts INATIVO
            return electorRepository.save(existingElector);
        }

        // Att as info do eleitor
        existingElector.setName(electorDetails.getName());
        existingElector.setCpf(electorDetails.getCpf());
        existingElector.setJob(electorDetails.getJob());
        existingElector.setMobilephone(electorDetails.getMobilephone());
        existingElector.setLandlinephone(electorDetails.getLandlinephone());
        existingElector.setEmail(electorDetails.getEmail());

        // Dfine o stts PENDENTE se CPF ou e-mail estiver faltando
        if (electorDetails.getCpf() == null || electorDetails.getEmail() == null) {
            existingElector.setStatus(Elector.Status.PENDENTE);
        } else {
            existingElector.setStatus(Elector.Status.APTO);
        }

        return electorRepository.save(existingElector);
    }


    @Transactional
    public String delete(Long id) {
        Elector elector = findById(id);

        if (elector == null) {
            throw new EntityNotFoundException("Eleitor não encontrado");
        }

        // se o eleitor ja votou
        if (elector.getStatus() == Elector.Status.VOTOU) {
            throw new IllegalStateException("Usuário já votou. Não foi possível inativá-lo.");
        }

        elector.setStatus(Elector.Status.INATIVO);
        electorRepository.save(elector);
        return "Eleitor inativado com sucesso";
    }

    public Elector findById(Long id) {
        Optional<Elector> elector = electorRepository.findById(id);
        return elector.orElse(null);
    }

    public List<Elector> findAll() {
        // Retorna apenas eleitores com stts APTO
        return electorRepository.findByStatus(Elector.Status.APTO);
    }


    @Transactional
    public void vote(Long id) {
        Elector elector = findById(id);

        if (elector == null) {
            throw new EntityNotFoundException("Eleitor não encontrado");
        }

        // Ve se o eleitor está apto pra votar
        if (elector.getStatus() == Elector.Status.APTO) {
            elector.setStatus(Elector.Status.VOTOU);
            electorRepository.save(elector);
        } else if (elector.getStatus() == Elector.Status.PENDENTE) {
            elector.setStatus(Elector.Status.BLOQUEADO);
            electorRepository.save(elector);
            throw new IllegalStateException("Usuário com cadastro pendente tentou votar. O usuário foi bloqueado!");
        } else {
            throw new IllegalStateException("Usuário não está apto para votar.");
        }
    }

}
