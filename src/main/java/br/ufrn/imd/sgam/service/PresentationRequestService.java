package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.PresentationRequestCancelDTO;
import br.ufrn.imd.sgam.dto.PresentationRequestCreateDTO;
import br.ufrn.imd.sgam.dto.PresentationRequestDTO;
import br.ufrn.imd.sgam.enums.PresentationRequestStatus;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.PresentationRequestMapper;
import br.ufrn.imd.sgam.model.Event;
import br.ufrn.imd.sgam.model.MusicalGroup;
import br.ufrn.imd.sgam.model.PresentationRequest;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.EventRepository;
import br.ufrn.imd.sgam.repository.MusicalGroupRepository;
import br.ufrn.imd.sgam.repository.PresentationRequestRepository;
import br.ufrn.imd.sgam.repository.UserInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PresentationRequestService {

    private final PresentationRequestRepository presentationRequestRepository;
    private final EventRepository eventRepository;
    private final MusicalGroupRepository musicalGroupRepository;
    private final UserInfoRepository userInfoRepository;
    private final PresentationRequestMapper presentationRequestMapper;

    public PresentationRequestDTO save(PresentationRequestCreateDTO dto, Long solicitanteId) {
        Event event = eventRepository.findById(dto.eventId()).orElseThrow(
                () -> new ResourceNotFoundException("Evento não encontrado"));
        MusicalGroup musicalGroup = musicalGroupRepository.findById(dto.musicalGroupId()).orElseThrow(
                () -> new ResourceNotFoundException("Grupo musical não encontrado"));
        UserInfo solicitante = userInfoRepository.findById(solicitanteId).orElseThrow(
                () -> new ResourceNotFoundException("Solicitante não encontrado"));

        PresentationRequest presentationRequest = new PresentationRequest();
        presentationRequest.setEvent(event);
        presentationRequest.setMusicalGroup(musicalGroup);
        presentationRequest.setSolicitante(solicitante);
        presentationRequest.setStatus(PresentationRequestStatus.PENDENTE);

        return presentationRequestMapper.toDTO(presentationRequestRepository.save(presentationRequest));
    }

    public Page<PresentationRequestDTO> listPendingByCoordinator(Long coordinatorId, Pageable pageable) {
        return presentationRequestRepository.findByMusicalGroupCoordenadorIdAndStatus(
                coordinatorId,
                PresentationRequestStatus.PENDENTE,
                pageable
        ).map(presentationRequestMapper::toDTO);
    }

    public PresentationRequestDTO confirm(Long id, Long coordinatorId) {
        PresentationRequest presentationRequest = getEntity(id);
        validatePending(presentationRequest);
        validateCoordinator(presentationRequest, coordinatorId);
        validateScheduleAvailability(presentationRequest);

        presentationRequest.setStatus(PresentationRequestStatus.CONFIRMADO);

        return presentationRequestMapper.toDTO(presentationRequestRepository.save(presentationRequest));
    }

    public PresentationRequestDTO cancel(Long id, PresentationRequestCancelDTO dto, Long userId) {
        PresentationRequest presentationRequest = getEntity(id);
        validateCancellationPermission(presentationRequest, userId);

        if (presentationRequest.getStatus() == PresentationRequestStatus.CANCELADO) {
            throw new BusinessException("Solicitação de apresentação ja cancelada.", HttpStatus.CONFLICT);
        }

        presentationRequest.setStatus(PresentationRequestStatus.CANCELADO);
        presentationRequest.setCancellationReason(dto.cancellationReason());

        return presentationRequestMapper.toDTO(presentationRequestRepository.save(presentationRequest));
    }

    public Page<PresentationRequestDTO> listConfirmedByEvent(Long eventId, Pageable pageable) {
        eventRepository.findById(eventId).orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));

        return presentationRequestRepository.findByEventIdAndStatus(
                eventId,
                PresentationRequestStatus.CONFIRMADO,
                pageable
        ).map(presentationRequestMapper::toDTO);
    }

    private PresentationRequest getEntity(Long id) {
        return presentationRequestRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Solicitação de apresentação não encontrada"));
    }

    private void validatePending(PresentationRequest presentationRequest) {
        if (presentationRequest.getStatus() != PresentationRequestStatus.PENDENTE) {
            throw new BusinessException("Apenas solicitações pendentes podem ser confirmadas.", HttpStatus.CONFLICT);
        }
    }

    private void validateCoordinator(PresentationRequest presentationRequest, Long coordinatorId) {
        Long groupCoordinatorId = presentationRequest.getMusicalGroup().getCoordenador().getId();
        if (!groupCoordinatorId.equals(coordinatorId)) {
            throw new BusinessException("Usuario não coordena o grupo musical informado.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateCancellationPermission(PresentationRequest presentationRequest, Long userId) {
        Long solicitanteId = presentationRequest.getSolicitante().getId();
        Long coordinatorId = presentationRequest.getMusicalGroup().getCoordenador().getId();

        if (!solicitanteId.equals(userId) && !coordinatorId.equals(userId)) {
            throw new BusinessException("Usuario não pode cancelar esta solicitação.", HttpStatus.FORBIDDEN);
        }
    }

    private void validateScheduleAvailability(PresentationRequest presentationRequest) {
        boolean hasConflict = presentationRequestRepository.existsByMusicalGroupIdAndStatusAndEventDateTimeAndIdNot(
                presentationRequest.getMusicalGroup().getId(),
                PresentationRequestStatus.CONFIRMADO,
                presentationRequest.getEvent().getDateTime(),
                presentationRequest.getId()
        );

        if (hasConflict) {
            throw new BusinessException("Grupo musical ja possui apresentação confirmada neste horario.",
                    HttpStatus.CONFLICT);
        }
    }
}
