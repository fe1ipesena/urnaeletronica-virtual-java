package com.app.urna.entity;

import com.app.urna.entity.enums.CandidateFunction;
import com.app.urna.entity.enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ]+( [A-Za-zÀ-ÖØ-öø-ÿ]+)+$", message = "Deve conter nome e sobrenome!")
    @NotBlank(message = "O nome não pode ser nulo!")
    private String name;

    @CPF(message = "CPF inválido!")
    @NotBlank(message = "O CPF não deve ser nulo!")
    private String cpf;

    @Column(unique = true)
    @NotNull(message = "O número do candidato é obrigatório!")
    private Long candidateNumber;

    @NotNull(message = "A função é obrigatória!")
    @Enumerated(EnumType.STRING)
    private CandidateFunction candidateFunction;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Transient
    private int apuratedVotes;

}
