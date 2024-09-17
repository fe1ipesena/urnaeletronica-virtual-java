package com.app.urna.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;


@Data
public class VoteRequest {

    @NotNull(message = "O ID do eleitor não pode ser nulo")
    private Long electorId;

    @NotNull(message = "O ID do prefeito não pode ser nulo")
    private Long mayorId;

    @NotNull(message = "O ID do vereador não pode ser nulo")
    private Long councilorId;
}
