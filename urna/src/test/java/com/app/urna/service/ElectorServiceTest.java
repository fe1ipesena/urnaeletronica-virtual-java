package com.app.urna.service;

import com.app.urna.entity.Elector;
import com.app.urna.entity.enums.StatusElector;
import com.app.urna.repository.ElectorRepository;
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
public class ElectorServiceTest {

    @InjectMocks
    private ElectorService electorService;

    @Mock
    private ElectorRepository electorRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveElectorSuccess() {
        Elector elector = new Elector();
        elector.setCpf("235.223.790-47");
        elector.setEmail("teste@example.com");

        when(electorRepository.save(any(Elector.class))).thenReturn(elector);
        Elector result = electorService.save(elector);

        assertEquals(StatusElector.PENDENTE, result.getStatus());
        verify(electorRepository).save(elector);
    }

    @Test
    void saveElectorWithIncompleteInfo() {
        Elector elector = new Elector();
        elector.setCpf("");
        elector.setEmail("");

        when(electorRepository.save(any(Elector.class))).thenReturn(elector);
        Elector result = electorService.save(elector);

        assertEquals(StatusElector.APTO, result.getStatus());
        verify(electorRepository).save(elector);
    }


    @Test
    void updateElectorSuccess() {
        Elector existingElector = new Elector();
        existingElector.setId(1L);
        existingElector.setStatus(StatusElector.PENDENTE);

        Elector electorDetails = new Elector();
        electorDetails.setName("Alteração do Nome");
        electorDetails.setCpf("235.223.790-47");
        electorDetails.setEmail("alterado@gmail.com");

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(existingElector));
        when(electorRepository.save(any(Elector.class))).thenReturn(existingElector);
        Elector result = electorService.update(1L, electorDetails);

        assertEquals(existingElector, result);
        verify(electorRepository).save(existingElector);
    }

    @Test
    void updateElectorNotFound() {
        Elector electorDetails = new Elector();
        electorDetails.setName("Alteração do Nome");

        when(electorRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            electorService.update(1L, electorDetails);
        });

        assertEquals("Eleitor não encontrado", exception.getMessage());
    }

    @Test
    void deleteElectorSuccess() {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setStatus(StatusElector.APTO);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        when(electorRepository.save(any(Elector.class))).thenReturn(elector);

        String result = electorService.delete(1L);

        assertEquals("Eleitor inativado com sucesso", result);
        assertEquals(StatusElector.INATIVO, elector.getStatus());
        verify(electorRepository).save(elector);
    }

    @Test
    void deleteElectorNotFound() {
        when(electorRepository.findById(anyLong())).thenReturn(Optional.empty());
        Exception exception = assertThrows(EntityNotFoundException.class, () -> {
            electorService.delete(1L);
        });

        assertEquals("Eleitor não encontrado", exception.getMessage());
    }

    @Test
    void deleteElectorAlreadyVoted() {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setStatus(StatusElector.VOTOU);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            electorService.delete(1L);
        });

        assertEquals("Usuário já votou. Não foi possível inativá-lo.", exception.getMessage());
    }

    @Test
    void findAllSuccess() {
        List<Elector> electors = new ArrayList<>();
        electors.add(new Elector());

        when(electorRepository.findByStatus(StatusElector.APTO)).thenReturn(electors);
        List<Elector> result = electorService.findAll();

        assertEquals(electors, result);
        verify(electorRepository).findByStatus(StatusElector.APTO);
    }

    @Test
    void voteSuccess() {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setStatus(StatusElector.APTO);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        when(electorRepository.save(any(Elector.class))).thenReturn(elector);

        electorService.vote(1L);

        assertEquals(StatusElector.VOTOU, elector.getStatus());
        verify(electorRepository).save(elector);
    }

    @Test
    void voteNotEligible() {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setStatus(StatusElector.BLOQUEADO);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            electorService.vote(1L);
        });

        assertEquals("Usuário não está apto para votar.", exception.getMessage());
    }

    @Test
    void votePendente() {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setStatus(StatusElector.PENDENTE);

        when(electorRepository.findById(anyLong())).thenReturn(Optional.of(elector));
        when(electorRepository.save(any(Elector.class))).thenReturn(elector);

        Exception exception = assertThrows(IllegalStateException.class, () -> {
            electorService.vote(1L);
        });

        assertEquals("Usuário com cadastro pendente tentou votar. O usuário foi bloqueado!", exception.getMessage());
    }
}
