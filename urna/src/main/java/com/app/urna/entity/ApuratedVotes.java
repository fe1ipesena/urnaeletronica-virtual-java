package com.app.urna.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApuratedVotes {

    private int total;

    private List<Candidate> mayors; // prefeitos

    private List<Candidate> councilors; // vereadores
}
