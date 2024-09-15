package com.app.urna.controller;

import com.app.urna.entity.Elector;
import com.app.urna.service.ElectorService;
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
@RequestMapping("/api/electors")
public class ElectorController {

    @Autowired
    private ElectorService electorService;

    @PostMapping("/save")
    public ResponseEntity<Object> createElector(@Valid @RequestBody Elector elector, BindingResult result) {
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
            Elector savedElector = electorService.save(elector);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedElector);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao salvar eleitor");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getElectorById(@PathVariable Long id) {
        try {
            Elector elector = electorService.findById(id);
            return ResponseEntity.ok(elector);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Eleitor não encontrado");
        }
    }

    @PutMapping("update/{id}")
    public ResponseEntity<Object> updateElector(@PathVariable Long id, @Valid @RequestBody Elector elector, BindingResult result) {
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
            Elector updatedElector = electorService.update(id, elector);
            return ResponseEntity.ok(updatedElector);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao atualizar eleitor");
        }
    }

    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> deleteElector(@PathVariable Long id) {
        try {
            String message = electorService.delete(id);
            return ResponseEntity.ok(message);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/findAll")
    public ResponseEntity<Object> getAllElectors() {
        try {
            List<Elector> electors = electorService.findAll();
            return ResponseEntity.ok(electors);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Erro ao buscar eleitores");
        }
    }
}
