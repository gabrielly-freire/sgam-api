package br.ufrn.imd.sgam.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelPresentationRequestDTO(

        @NotBlank
        String cancellationReason

) { }

