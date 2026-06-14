package br.ufrn.imd.sgam.dto;

import jakarta.validation.constraints.NotNull;

public record PresentationRequestCreateDTO(
        @NotNull(message = "O ID do evento e obrigatório")
        Long eventId,

        @NotNull(message = "O ID do grupo musical e obrigatório")
        Long musicalGroupId
) {
}
