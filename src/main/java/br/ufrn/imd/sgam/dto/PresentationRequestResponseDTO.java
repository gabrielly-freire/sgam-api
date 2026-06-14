package br.ufrn.imd.sgam.dto;

public record PresentationRequestResponseDTO(

        Long id,

        Long eventId,
        String eventTitle,

        Long requesterId,
        String requesterName,

        Long musicalGroupId,
        String musicalGroupName,

        String status,

        String cancellationReason

) { }
