package com.app.urna.controller;

import com.app.urna.entity.Candidate;
import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.service.CandidateService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CandidateController.class)
public class CandidateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CandidateService candidateService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void saveCandidateSuccess() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setName("Leo Dias");
        candidate.setCpf("754.748.260-01");
        candidate.setCandidateNumber(123L);
        candidate.setCandidateFunction(CandidateFunction.MAYOR);

        Candidate savedCandidate = new Candidate();
        savedCandidate.setId(1L);
        savedCandidate.setName("Leo Dias Pinto");
        savedCandidate.setCpf("754.748.260-01");
        savedCandidate.setCandidateNumber(123L);
        savedCandidate.setCandidateFunction(CandidateFunction.MAYOR);

        when(candidateService.save(any(Candidate.class))).thenReturn(savedCandidate);

        mockMvc.perform(MockMvcRequestBuilders.post("/api/candidates/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }


    @Test
    void saveCandidateFailure() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setCandidateFunction(null); // Simula falha

        mockMvc.perform(MockMvcRequestBuilders.post("/api/candidates/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(org.hamcrest.Matchers.containsString("A função é obrigatória!")));
    }

    @Test
    void deleteCandidateSuccess() throws Exception {
        when(candidateService.delete(anyLong())).thenReturn("Candidato deletado com sucesso");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/candidates/delete/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Candidato deletado com sucesso"));
    }

    @Test
    void updateCandidateSuccess() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setName("Gilmar Costa Neves");
        candidate.setCpf("727.918.380-82");
        candidate.setCandidateNumber(123L);
        candidate.setCandidateFunction(CandidateFunction.COUNCILOR);

        when(candidateService.update(anyLong(), any(Candidate.class))).thenReturn(candidate);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/candidates/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    void updateCandidateFailure() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setName("Felipe Yuto");
        candidate.setCpf("318.174.750-53");
        candidate.setCandidateNumber(123L);
        candidate.setCandidateFunction(CandidateFunction.COUNCILOR);

        when(candidateService.update(anyLong(), any(Candidate.class))).thenThrow(new EntityNotFoundException("Candidato não encontrado"));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/candidates/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Candidato não encontrado"));
    }

    @Test
    void getCandidateByIdSuccess() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setCandidateFunction(CandidateFunction.MAYOR);

        when(candidateService.findById(anyLong())).thenReturn(candidate);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/candidates/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    void getCandidateByIdFailure() throws Exception {
        when(candidateService.findById(anyLong())).thenThrow(new EntityNotFoundException("Candidato não encontrado"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/candidates/1"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Candidato não encontrado"));
    }

    @Test
    void getAllCandidates() throws Exception {
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate());

        when(candidateService.findAll()).thenReturn(candidates);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/candidates/findAll"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }


    @Test
    void getActiveCandidatesForCouncilorError() throws Exception {
        when(candidateService.findActiveCandidatesForCouncilor()).thenThrow(new RuntimeException("Erro ao buscar candidatos ativos para vereador"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/candidates/active/councilor"))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Erro ao buscar candidatos ativos para vereador: Erro ao buscar candidatos ativos para vereador"));
    }

    @Test
    void getActiveCandidatesForMayorError() throws Exception {
        when(candidateService.findActiveCandidatesForMayor()).thenThrow(new RuntimeException("Erro ao buscar candidatos ativos para prefeito"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/candidates/active/mayor"))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Erro ao buscar candidatos ativos para prefeito: Erro ao buscar candidatos ativos para prefeito"));
    }


    @Test
    void createCandidateUnexpectedError() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setName("Jorginho Costa");
        candidate.setCandidateFunction(CandidateFunction.MAYOR);
        candidate.setCpf("700.886.470-13");
        candidate.setCandidateNumber(123L);

        when(candidateService.save(any(Candidate.class))).thenThrow(new RuntimeException("Erro inesperado"));
        mockMvc.perform(MockMvcRequestBuilders.post("/api/candidates/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Erro ao salvar candidato"));
    }

    @Test
    void getActiveCandidatesByPositionSuccess() throws Exception {
        List<Candidate> candidates = new ArrayList<>();
        candidates.add(new Candidate());

        when(candidateService.findActiveCandidatesByPosition(any(CandidateFunction.class))).thenReturn(candidates);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/candidates/active/position/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0]").exists());
    }

    @Test
    void getActiveCandidatesByPositionFailure() throws Exception {
        when(candidateService.findActiveCandidatesByPosition(any(CandidateFunction.class)))
                .thenThrow(new IllegalArgumentException("Função inválida: 999"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/candidates/active/position/999"))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Função inválida: 999"));
    }

    @Test
    void getAllCandidatesError() throws Exception {
        when(candidateService.findAll()).thenThrow(new RuntimeException("Erro ao buscar candidatos"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/candidates/findAll"))
                .andExpect(status().isInternalServerError())
                .andExpect(MockMvcResultMatchers.content().string("Erro ao buscar candidatos"));
    }


    @Test
    void updateCandidateIllegalArgumentException() throws Exception {
        Candidate candidate = new Candidate();
        candidate.setId(1L);
        candidate.setName("Pedro Alvarez");
        candidate.setCpf("864.995.350-60");
        candidate.setCandidateNumber(123L);
        candidate.setCandidateFunction(CandidateFunction.COUNCILOR);

        when(candidateService.update(anyLong(), any(Candidate.class)))
                .thenThrow(new IllegalArgumentException("Argumento inválido"));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/candidates/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(candidate)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string("Argumento inválido"));
    }


}
