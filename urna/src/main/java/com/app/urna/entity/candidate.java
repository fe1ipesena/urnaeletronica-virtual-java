package com.app.urna.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
public class candidate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ]+( [A-Za-zÀ-ÖØ-öø-ÿ]+)+$", message = "Deve ser nome e sobrenome.")
    @NotBlank
    private String name;

    @CPF(message = "CPF inválido")
    private String cpf;

    @Column(unique = true)
    private int candidateNumber;

    @Pattern(regexp = "^[12]$", message = "Precisa escolher entre as opções: [1]Prefeito ou [2]Vereador.")
    private String candidateFunction;

    private enum status{
        ATIVO, INATIVO;
    }

    @Transient
    private apuratedVotes ApuratedVotes;

}
