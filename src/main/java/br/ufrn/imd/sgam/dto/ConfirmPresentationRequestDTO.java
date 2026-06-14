package br.ufrn.imd.sgam.dto;

import jakarta.validation.constraints.NotNull;

public record ConfirmPresentationRequestDTO(

        @NotNull
        Long musicalGroupId

) { }
