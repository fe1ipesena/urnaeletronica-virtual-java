package com.app.urna.controller;

import com.app.urna.entity.Elector;
import com.app.urna.service.ElectorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;


import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ElectorController.class)
public class ElectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ElectorService electorService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createElectorSuccess() throws Exception {
        Elector elector = new Elector();
        elector.setName("Maria Silva");
        elector.setCpf("759.818.330-92");
        elector.setJob("Pedreira");
        elector.setMobilephone("(12) 34567-8901");

        Elector savedElector = new Elector();
        savedElector.setId(1L);
        savedElector.setName("Maria Silva");
        savedElector.setCpf("759.818.330-92");
        savedElector.setJob("Pedreira");
        savedElector.setMobilephone("(12) 34567-8901");

        when(electorService.save(any(Elector.class))).thenReturn(savedElector);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/electors/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(elector)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    void createElectorFailure() throws Exception {
        Elector elector = new Elector();
        elector.setCpf(""); // CPF inválido

        mockMvc.perform(MockMvcRequestBuilders.post("/api/electors/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(elector)))
                .andExpect(status().isBadRequest())
                .andExpect(MockMvcResultMatchers.content().string(
                        org.hamcrest.Matchers.allOf(
                                org.hamcrest.Matchers.containsString("cpf: CPF inválido"),
                                org.hamcrest.Matchers.containsString("name: Não pode ser nulo"),
                                org.hamcrest.Matchers.containsString("job: Não pode ser nulo")
                        )
                ));
    }



    @Test
    void updateElectorSuccess() throws Exception {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setName("Maria Silva");
        elector.setCpf("759.818.330-92");
        elector.setJob("Desenvolvedor");
        elector.setMobilephone("(12) 34567-8901");

        when(electorService.update(anyLong(), any(Elector.class))).thenReturn(elector);
        mockMvc.perform(MockMvcRequestBuilders.put("/api/electors/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(elector)))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").value("Maria Silva"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cpf").value("759.818.330-92"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.job").value("Desenvolvedor"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.mobilephone").value("(12) 34567-8901"));
    }


    @Test
    void updateElectorFailure() throws Exception {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setName("Maria Silva");
        elector.setCpf("759.818.330-92");
        elector.setJob("Desenvolvedor");
        elector.setMobilephone("(12) 34567-8901");

        when(electorService.update(anyLong(), any(Elector.class))).thenThrow(new EntityNotFoundException("Eleitor não encontrado"));
        mockMvc.perform(MockMvcRequestBuilders.put("/api/electors/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(elector)))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Eleitor não encontrado"));
    }

    @Test
    void deleteElectorSuccess() throws Exception {
        when(electorService.delete(anyLong())).thenReturn("Eleitor deletado com sucesso");
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/electors/delete/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("Eleitor deletado com sucesso"));
    }

    @Test
    void deleteElectorFailure() throws Exception {
        when(electorService.delete(anyLong())).thenThrow(new EntityNotFoundException("Eleitor não encontrado"));
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/electors/delete/1"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Eleitor não encontrado"));
    }

    @Test
    void getElectorByIdSuccess() throws Exception {
        Elector elector = new Elector();
        elector.setId(1L);
        elector.setName("Maria Silva");
        elector.setCpf("759.818.330-92");

        when(electorService.findById(anyLong())).thenReturn(elector);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/electors/1"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(1L));
    }

    @Test
    void getElectorByIdFailure() throws Exception {
        when(electorService.findById(anyLong())).thenThrow(new EntityNotFoundException("Eleitor não encontrado"));
        mockMvc.perform(MockMvcRequestBuilders.get("/api/electors/1"))
                .andExpect(status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Eleitor não encontrado"));
    }

    @Test
    void getAllElectors() throws Exception {
        List<Elector> electors = new ArrayList<>();
        Elector elector = new Elector();
        elector.setName("Maria Silva");
        elector.setCpf("759.818.330-92");
        electors.add(elector);


        when(electorService.findAll()).thenReturn(electors);
        mockMvc.perform(MockMvcRequestBuilders.get("/api/electors/findAll"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].cpf").value("759.818.330-92"));
    }
}
