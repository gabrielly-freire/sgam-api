package br.ufrn.imd.sgam.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record MusicalGroupDTO(
        Long id,

        @NotBlank(message = "O nome do grupo é obrigatório")
        @Size(min = 2, max = 100, message = "O nome do grupo deve ter entre 2 e 100 caracteres")
        String nome,

        @NotNull(message = "O ID do coordenador é obrigatório")
        Long coordenadorId
) {
}