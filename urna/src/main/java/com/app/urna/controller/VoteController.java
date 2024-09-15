package com.app.urna.controller;

import com.app.urna.entity.ApuratedVotes;
import com.app.urna.entity.Vote;
import com.app.urna.service.VoteService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/votes")
public class VoteController {

    @Autowired
    private VoteService voteService;

    @PostMapping("/vote")
    public ResponseEntity<String> vote(@Valid @RequestBody Vote vote, @RequestParam Long electorId, BindingResult result) {
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
            String hash = voteService.vote(vote, electorId);
            return ResponseEntity.ok(hash);

        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);

        } catch (Exception e) {
            return new ResponseEntity<>("Ocorreu um erro inesperado: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/perform-counting")
    public ResponseEntity<Object> performCounting() {
        try {
            ApuratedVotes result = voteService.performCounting();
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
