package com.app.urna.service;

import com.app.urna.entity.Elector;
import com.app.urna.entity.enums.StatusElector;
import com.app.urna.repository.ElectorRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class ElectorService {

    @Autowired
    private ElectorRepository electorRepository;

    @Transactional
    public Elector save(Elector elector) {
        // Define status como APTO se ambos CPF e e-mail estiverem preenchidos
        if (StringUtils.hasText(elector.getCpf()) && StringUtils.hasText(elector.getEmail())) {
            elector.setStatus(StatusElector.APTO);
        } else {
            // Define status como PENDENTE se CPF ou e-mail estiver faltando
            elector.setStatus(StatusElector.PENDENTE);
        }

        return electorRepository.save(elector);
    }

    @Transactional
    public Elector update(Long id, Elector electorDetails) {
        Elector existingElector = findById(id);

        if (existingElector == null) {
            throw new EntityNotFoundException("Eleitor não encontrado");
        }

        // Se o eleitor já está INATIVO, atualiza as informações, mas mantém o status como INATIVO
        if (existingElector.getStatus() == StatusElector.INATIVO) {
            updateElectorInfo(existingElector, electorDetails);
            return electorRepository.save(existingElector); // Mantém o status como INATIVO
        }

        // Atualiza as informações do eleitor
        updateElectorInfo(existingElector, electorDetails);

        // Define o status com base no preenchimento de CPF ou e-mail
        if (StringUtils.hasText(electorDetails.getCpf()) || StringUtils.hasText(electorDetails.getEmail())) {
            existingElector.setStatus(StatusElector.APTO); // Define como APTO se houver CPF ou e-mail
        } else {
            existingElector.setStatus(StatusElector.PENDENTE); // Define como PENDENTE se ambos estiverem vazios
        }

        return electorRepository.save(existingElector);
    }


    @Transactional
    public String delete(Long id) {
        Elector elector = findById(id);

        if (elector == null) {
            throw new EntityNotFoundException("Eleitor não encontrado");
        }

        if (elector.getStatus() == StatusElector.VOTOU) {
            throw new IllegalStateException("Usuário já votou. Não foi possível inativá-lo.");
        }

        elector.setStatus(StatusElector.INATIVO);
        electorRepository.save(elector);
        return "Eleitor inativado com sucesso";
    }

    public Elector findById(Long id) {
        Optional<Elector> elector = electorRepository.findById(id);
        return elector.orElseThrow(() -> new EntityNotFoundException("Eleitor não encontrado"));
    }

    public List<Elector> findAll() {
        return electorRepository.findByStatus(StatusElector.APTO);
    }

    @Transactional
    public void vote(Long id) {
        Elector elector = findById(id);

        if (elector == null) {
            throw new EntityNotFoundException("Eleitor não encontrado");
        }

        if (elector.getStatus() == StatusElector.APTO) {
            elector.setStatus(StatusElector.VOTOU);
            electorRepository.save(elector);
        } else if (elector.getStatus() == StatusElector.PENDENTE) {
            elector.setStatus(StatusElector.BLOQUEADO);
            electorRepository.save(elector);
            throw new IllegalStateException("Usuário com cadastro pendente tentou votar. O usuário foi bloqueado!");
        } else {
            throw new IllegalStateException("Usuário não está apto para votar.");
        }
    }

    private void updateElectorInfo(Elector existingElector, Elector electorDetails) {
        existingElector.setName(electorDetails.getName());
        existingElector.setCpf(electorDetails.getCpf());
        existingElector.setJob(electorDetails.getJob());
        existingElector.setMobilephone(electorDetails.getMobilephone());
        existingElector.setLandlinephone(electorDetails.getLandlinephone());
        existingElector.setEmail(electorDetails.getEmail());
    }
}
