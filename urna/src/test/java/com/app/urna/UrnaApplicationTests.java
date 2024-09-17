package com.app.urna;

import com.app.urna.controller.CandidateController;
import com.app.urna.service.CandidateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class UrnaApplicationTests {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        // Verifica se o contexto do Spring está carregando sem problemas
    }

    @Test
    void testCandidateControllerIsLoaded() {
        assertThat(applicationContext.getBean(CandidateController.class)).isNotNull();
    }

    @Test
    void testCandidateServiceIsLoaded() {
        assertThat(applicationContext.getBean(CandidateService.class)).isNotNull();
    }
}
