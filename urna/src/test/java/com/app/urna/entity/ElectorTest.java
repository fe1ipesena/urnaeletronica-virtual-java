package com.app.urna.entity;

import com.app.urna.entity.enums.StatusElector;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ElectorTest {

    private Validator validator;

    @BeforeEach
    public void setup() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        this.validator = factory.getValidator();
    }

    @Test
    public void testValidElector() {
        Elector elector = new Elector();
        elector.setName("Jota Pe");
        elector.setCpf("628.245.090-07"); // CPF válido para teste
        elector.setJob("Engineer");
        elector.setMobilephone("(12) 34567-8901");
        elector.setLandlinephone("(12) 3456-7890");
        elector.setEmail("jp.ex@yahoo.com.br");
        elector.setStatus(StatusElector.APTO);

        Set<ConstraintViolation<Elector>> violations = validator.validate(elector);
        assertTrue(violations.isEmpty(), "Não deve haver violações para um eleitor válido");
    }

    @Test
    public void testInvalidElectorName() {
        Elector elector = new Elector();
        elector.setName("John"); // Nome inválido (deve ter pelo menos nome e sobrenome)
        elector.setCpf("628.245.090-07");
        elector.setJob("Engineer");
        elector.setMobilephone("(12) 34567-8901");
        elector.setLandlinephone("(12) 3456-7890");
        elector.setEmail("jp.ex@yahoo.com.br");
        elector.setStatus(StatusElector.APTO);

        Set<ConstraintViolation<Elector>> violations = validator.validate(elector);
        assertFalse(violations.isEmpty(), "deve ter pelo menos nome e sobrenome");
    }

    @Test
    public void testInvalidElectorCPF() {
        Elector elector = new Elector();
        elector.setName("Jota Pe");
        elector.setCpf("123.456.789-00"); // CPF inválido para teste
        elector.setJob("Engineer");
        elector.setMobilephone("(12) 34567-8901");
        elector.setLandlinephone("(12) 3456-7890");
        elector.setEmail("jp.ex@yahoo.com.br");
        elector.setStatus(StatusElector.APTO);

        Set<ConstraintViolation<Elector>> violations = validator.validate(elector);
        assertFalse(violations.isEmpty(), "CPF inválido");
    }

    @Test
    public void testInvalidElectorPhone() {
        Elector elector = new Elector();
        elector.setName("Jota Pe");
        elector.setCpf("628.245.090-07");
        elector.setJob("Engineer");
        elector.setMobilephone("invalid-phone"); // Telefone inválido
        elector.setLandlinephone("(12) 3456-7890");
        elector.setEmail("jp.ex@yahoo.com.br");
        elector.setStatus(StatusElector.APTO);

        Set<ConstraintViolation<Elector>> violations = validator.validate(elector);
        assertFalse(violations.isEmpty(), "Telefone inválido");
    }

    @Test
    public void testInvalidElectorEmail() {
        Elector elector = new Elector();
        elector.setName("Jota Pe");
        elector.setCpf("628.245.090-07");
        elector.setJob("Engineer");
        elector.setMobilephone("(12) 34567-8901");
        elector.setLandlinephone("(12) 3456-7890");
        elector.setEmail("invalid-email"); // Email inválido
        elector.setStatus(StatusElector.APTO);

        Set<ConstraintViolation<Elector>> violations = validator.validate(elector);
        assertFalse(violations.isEmpty(), "Email inválido");
    }

//    @Test
//    public void testInvalidElectorStatus() {
//        Elector elector = new Elector();
//        elector.setName("Jota Pe");
//        elector.setCpf("628.245.090-07");
//        elector.setJob("Engineer");
//        elector.setMobilephone("(12) 34567-8901");
//        elector.setLandlinephone("(12) 3456-7890");
//        elector.setEmail("jp.ex@yahoo.com.br);
//        elector.setStatus(null); // Status nulo
//
//        Set<ConstraintViolation<Elector>> violations = validator.validate(elector);
//        assertFalse(violations.isEmpty(), "Should have violations for null status");
//    }
}
