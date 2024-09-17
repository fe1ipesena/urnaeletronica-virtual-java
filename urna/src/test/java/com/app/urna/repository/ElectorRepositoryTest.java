package com.app.urna.repository;

import com.app.urna.entity.Elector;
import com.app.urna.entity.enums.StatusElector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ElectorRepositoryTest {

    @Autowired
    private ElectorRepository electorRepository;

    @Test
    public void testFindByStatus() {
        Elector elector = new Elector();
        elector.setStatus(StatusElector.APTO);
        elector.setCpf("628.245.090-07");
        elector.setName("Jão Silva");
        elector.setJob("Desenvolvedor");
        elector.setMobilephone("(11) 12345-6789");
        elector.setLandlinephone("(11) 1234-5678");
        elector.setEmail("joao.silva@example.com");

        electorRepository.save(elector);

        List<Elector> electors = electorRepository.findByStatus(StatusElector.APTO);
        assertThat(electors).isNotEmpty();
    }
}
