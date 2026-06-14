package br.ufrn.imd.sgam.dto;

import br.ufrn.imd.sgam.enums.PresentationRequestStatus;

import java.time.LocalDateTime;

public record PresentationRequestDTO(
        Long id,
        Long eventId,
        String eventTitle,
        LocalDateTime eventDateTime,
        Long musicalGroupId,
        String musicalGroupName,
        Long solicitanteId,
        String solicitanteName,
        PresentationRequestStatus status,
        String cancellationReason
) {
}
