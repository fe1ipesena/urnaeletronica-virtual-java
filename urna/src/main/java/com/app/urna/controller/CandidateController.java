package com.app.urna.controller;

import com.app.urna.entity.Candidate;
import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.service.CandidateService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/candidates")
public class CandidateController {

    @Autowired
    private CandidateService candidateService;

    @PostMapping("/save")
    public ResponseEntity<Object> createCandidate(@Valid @RequestBody Candidate candidate, BindingResult result) {
        if (result.hasErrors()) { // Primeiro, verifique se há erros de validação
            StringBuilder errorMsg = new StringBuilder("--> ");
            result.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String defaultMessage = error.getDefaultMessage();
                errorMsg.append(fieldName).append(": ").append(defaultMessage).append("\n");
            });
            return new ResponseEntity<>(errorMsg.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            Candidate savedCandidate = candidateService.save(candidate);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCandidate);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar candidato");
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<Object> getCandidateById(@PathVariable Long id) {
        try {
            Candidate candidate = candidateService.findById(id);
            return ResponseEntity.ok(candidate);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Candidato não encontrado");
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Object> updateCandidate(@PathVariable Long id, @Valid @RequestBody Candidate candidate, BindingResult result) {
        if (result.hasErrors()) {
            StringBuilder errorMsg = new StringBuilder("--> ");
            result.getAllErrors().forEach(error -> {
                String fieldName = ((FieldError) error).getField();
                String defaultMessage = error.getDefaultMessage();
                errorMsg.append(fieldName).append(": ").append(defaultMessage).append("\n");
            });
            return new ResponseEntity<>(errorMsg.toString(), HttpStatus.BAD_REQUEST);
        }
        try {
            Candidate updatedCandidate = candidateService.update(id, candidate);
            return ResponseEntity.ok(updatedCandidate);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar candidato");
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteCandidate(@PathVariable Long id) {
        try {
            String message = candidateService.delete(id);
            return ResponseEntity.ok(message);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<Object> getAllCandidates() {
        try {
            List<Candidate> candidates = candidateService.findAll();
            return ResponseEntity.ok(candidates);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar candidatos");
        }
    }

    @GetMapping("/active/position/{position}")
    public ResponseEntity<?> getActiveCandidatesByPosition(@PathVariable int position) {
        try {
            CandidateFunction function = CandidateFunction.fromValue(position);
            List<Candidate> candidates = candidateService.findActiveCandidatesByPosition(function);
            return ResponseEntity.ok(candidates);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro inesperado: " + e.getMessage());
        }
    }

    @GetMapping("/active/mayor")
    public ResponseEntity<Object> getActiveCandidatesForMayor() {
        try {
            List<Candidate> candidates = candidateService.findActiveCandidatesForMayor();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar candidatos ativos para prefeito: " + e.getMessage());
        }
    }

    @GetMapping("/active/councilor")
    public ResponseEntity<Object> getActiveCandidatesForCouncilor() {
        try {
            List<Candidate> candidates = candidateService.findActiveCandidatesForCouncilor();
            return ResponseEntity.ok(candidates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar candidatos ativos para vereador: " + e.getMessage());
        }
    }
}

