package com.app.urna.entity.enums;

import lombok.Getter;

@Getter
public enum CandidateFunction {
    NO_FUNCTION(0),
    MAYOR(1),       // Prefeito
    COUNCILOR(2);   // Vereador

    private final int value;

    CandidateFunction(int value) {
        this.value = value;
    }

    public static CandidateFunction fromValue(int value) {
        for (CandidateFunction function : values()) {
            if (function.getValue() == value) {
                return function;
            }
        }
        throw new IllegalArgumentException("Função inválida: " + value);
    }
}
