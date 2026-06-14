package br.ufrn.imd.sgam.dto;

import jakarta.validation.constraints.NotBlank;

public record PresentationRequestCancelDTO(
        @NotBlank(message = "O motivo do cancelamento e obrigatório")
        String cancellationReason
) {
}
