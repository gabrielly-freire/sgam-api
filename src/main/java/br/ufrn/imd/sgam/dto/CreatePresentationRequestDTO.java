package br.ufrn.imd.sgam.dto;

import jakarta.validation.constraints.NotNull;

public record CreatePresentationRequestDTO(

        @NotNull
        Long eventId

) { }
