package com.app.urna.controller;

import com.app.urna.dto.VoteRequest;
import com.app.urna.entity.ApuratedVotes;
import com.app.urna.service.VoteService;
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
    public ResponseEntity<String> vote(@Valid @RequestBody VoteRequest voteRequest, BindingResult result) {
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
            String hash = voteService.vote(voteRequest.getElectorId(), voteRequest.getMayorId(), voteRequest.getCouncilorId());
            return ResponseEntity.ok(hash);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
