package com.app.urna.entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
//testedesbuagaaaa
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class apuratedVotes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int total;

    private List prefeitos;

    private List vereadores;

}
