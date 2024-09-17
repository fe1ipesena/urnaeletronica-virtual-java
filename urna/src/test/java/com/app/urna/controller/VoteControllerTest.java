package com.app.urna.controller;

import com.app.urna.dto.VoteRequest;
import com.app.urna.entity.ApuratedVotes;
import com.app.urna.service.VoteService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoteController.class)
public class VoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VoteService voteService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setup() {

    }

    @Test
    void voteSuccess() throws Exception {
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setElectorId(1L);
        voteRequest.setMayorId(1L);
        voteRequest.setCouncilorId(1L);

        Mockito.when(voteService.vote(1L, 1L, 1L)).thenReturn("valid_hash");
        mockMvc.perform(MockMvcRequestBuilders.post("/votes/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("valid_hash"));
    }

    @Test
    void voteValidationFailure() throws Exception {
        VoteRequest voteRequest = new VoteRequest();

        mockMvc.perform(MockMvcRequestBuilders.post("/votes/vote")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(voteRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        org.hamcrest.Matchers.containsString("electorId: O ID do eleitor não pode ser nulo") // Ajuste a mensagem conforme a resposta real
                ))
                .andExpect(MockMvcResultMatchers.content().string(
                        org.hamcrest.Matchers.containsString("mayorId: O ID do prefeito não pode ser nulo") // Ajuste a mensagem conforme a resposta real
                ))
                .andExpect(MockMvcResultMatchers.content().string(
                        org.hamcrest.Matchers.containsString("councilorId: O ID do vereador não pode ser nulo") // Ajuste a mensagem conforme a resposta real
                ));
    }


    @Test
    void performCountingSuccess() throws Exception {
        ApuratedVotes apuratedVotes = new ApuratedVotes();

        Mockito.when(voteService.performCounting()).thenReturn(apuratedVotes);
        mockMvc.perform(MockMvcRequestBuilders.get("/votes/perform-counting"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().json(objectMapper.writeValueAsString(apuratedVotes)));
    }

    @Test
    void performCountingFailure() throws Exception {
        Mockito.when(voteService.performCounting()).thenThrow(new RuntimeException("Erro na contagem"));
        mockMvc.perform(MockMvcRequestBuilders.get("/votes/perform-counting"))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Erro na contagem"));
    }
}
