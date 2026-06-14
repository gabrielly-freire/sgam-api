package br.ufrn.imd.sgam.mapper;

import br.ufrn.imd.sgam.dto.PresentationRequestDTO;
import br.ufrn.imd.sgam.model.Event;
import br.ufrn.imd.sgam.model.MusicalGroup;
import br.ufrn.imd.sgam.model.PresentationRequest;
import br.ufrn.imd.sgam.model.UserInfo;
import org.springframework.stereotype.Component;

@Component
public class PresentationRequestMapper {

    public PresentationRequestDTO toDTO(PresentationRequest presentationRequest) {
        if (presentationRequest == null) {
            return null;
        }

        Event event = presentationRequest.getEvent();
        MusicalGroup musicalGroup = presentationRequest.getMusicalGroup();
        UserInfo solicitante = presentationRequest.getSolicitante();

        return new PresentationRequestDTO(
                presentationRequest.getId(),
                event != null ? event.getId() : null,
                event != null ? event.getTitle() : null,
                event != null ? event.getDateTime() : null,
                musicalGroup != null ? musicalGroup.getId() : null,
                musicalGroup != null ? musicalGroup.getNome() : null,
                solicitante != null ? solicitante.getId() : null,
                solicitante != null ? solicitante.getName() : null,
                presentationRequest.getStatus(),
                presentationRequest.getCancellationReason()
        );
    }
}
