package com.app.urna.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
public class Elector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Pattern(regexp = "^[A-Za-zÀ-ÖØ-öø-ÿ]+( [A-Za-zÀ-ÖØ-öø-ÿ]+)+$", message = "Deve ser nome e sobrenome.")
    @NotBlank
    private String name;

    @CPF(message = "CPF inválido")
    private String cpf;

    @NotBlank
    private String job;

    @Pattern(regexp = "^\\(\\d{2}\\) \\d{4,5}-\\d{4}$", message = "O telefone deve seguir o padrão: (XX) XXXX-XXXX ou (XX) XXXXX-XXXX")
    @NotBlank(message = "É preciso apresentar um telefone")
    private String mobilephone;

    @Pattern(regexp = "^\\(\\d{2}\\) \\d{4}-\\d{4}$", message = "Telefone fixo inválido!")
    private String landlinephone;

    @Email(message = "E-mail Inválido. O formato deve ser exemplo@exemplo.com.")
    private String email;

    @Enumerated(EnumType.STRING)
    private Status status;

    public enum Status {
        INATIVO, PENDENTE, BLOQUEADO, VOTOU, APTO
    }
}
