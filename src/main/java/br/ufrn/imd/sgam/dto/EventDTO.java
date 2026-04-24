package br.ufrn.imd.sgam.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record EventDTO(
        Long id,

        @NotBlank(message = "O título do evento é obrigatório")
        String title,

        @NotNull(message = "A data e hora são obrigatórias")
        @FutureOrPresent(message = "A data do evento não pode ser no passado")
        LocalDateTime dateTime,

        @NotBlank(message = "O local é obrigatório")
        String location,

        String description
) {
}