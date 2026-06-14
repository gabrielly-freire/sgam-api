package br.ufrn.imd.sgam.mapper;

import br.ufrn.imd.sgam.dto.PresentationRequestResponseDTO;
import br.ufrn.imd.sgam.model.PresentationRequest;
import org.springframework.stereotype.Component;

@Component
public class PresentationRequestMapper {

    public PresentationRequestResponseDTO toDTO(
            PresentationRequest entity
    ) {

        return new PresentationRequestResponseDTO(

                entity.getId(),

                entity.getEvent().getId(),
                entity.getEvent().getTitle(),

                entity.getRequester().getId(),
                entity.getRequester().getName(),

                entity.getMusicalGroup() != null
                        ? entity.getMusicalGroup().getId()
                        : null,

                entity.getMusicalGroup() != null
                        ? entity.getMusicalGroup().getNome()
                        : null,

                entity.getStatus().name(),

                entity.getCancellationReason()
        );
    }
}
