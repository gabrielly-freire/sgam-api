package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.*;
import br.ufrn.imd.sgam.enums.PresentationRequestStatus;
import br.ufrn.imd.sgam.enums.RequestStatus;
import br.ufrn.imd.sgam.mapper.PresentationRequestMapper;
import br.ufrn.imd.sgam.model.*;
import br.ufrn.imd.sgam.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PresentationRequestService {

        private final PresentationRequestRepository repository;
        private final EventRepository eventRepository;
        private final MusicalGroupRepository musicalGroupRepository;
        private final PresentationRequestMapper mapper;

        private boolean isAdmin(UserInfo user) {
                return user.getRole().name().equals("ADMIN");
        }

        private boolean isCoordinator(UserInfo user) {
                return user.getRole().name().equals("COORDENADOR");
        }

        @Transactional
        public PresentationRequestResponseDTO create(
                        CreatePresentationRequestDTO dto,
                        UserInfo requester) {

                Event event = eventRepository.findById(dto.eventId())
                                .orElseThrow();

                PresentationRequest request = new PresentationRequest();

                request.setEvent(event);
                request.setRequester(requester);
                request.setStatus(RequestStatus.PENDENTE);

                repository.save(request);

                return mapper.toDTO(request);
        }

        @Transactional
        public void confirm(
                        Long requestId,
                        ConfirmPresentationRequestDTO dto,
                        UserInfo currentUser) {

                PresentationRequest request = repository.findById(requestId)
                                .orElseThrow();

                MusicalGroup group = musicalGroupRepository
                                .findById(dto.musicalGroupId())
                                .orElseThrow();

                if (!group.getCoordenador()
                                .getId()
                                .equals(currentUser.getId())) {

                        throw new RuntimeException(
                                        "Você não coordena este grupo.");
                }

                boolean alreadyBusy = repository
                                .existsConfirmedPresentationAtSameTime(
                                                group.getId(),
                                                request.getEvent()
                                                                .getDateTime());

                if (alreadyBusy) {

                        throw new RuntimeException(
                                        "Grupo já possui apresentação confirmada neste horário.");
                }

                request.setMusicalGroup(group);
                request.setStatus(RequestStatus.CONFIRMADO);

                repository.save(request);
        }

        @Transactional
        public void cancel(
                        Long requestId,
                        CancelPresentationRequestDTO dto,
                        UserInfo currentUser) {

                PresentationRequest request = repository.findById(requestId)
                                .orElseThrow();

                boolean isRequester = request.getRequester()
                                .getId()
                                .equals(currentUser.getId());

                boolean isCoordinator = request.getMusicalGroup() != null
                                &&
                                request.getMusicalGroup()
                                                .getCoordenador()
                                                .getId()
                                                .equals(currentUser.getId());

                if (!isRequester && !isCoordinator) {

                        throw new RuntimeException(
                                        "Você não possui permissão para cancelar esta solicitação.");
                }

                request.setStatus(
                                RequestStatus.CANCELADO);

                request.setCancellationReason(
                                dto.cancellationReason());

                repository.save(request);
        }

        public List<PresentationRequestResponseDTO> listPending(UserInfo currentUser) {

                if (!isCoordinator(currentUser)) {

                        throw new RuntimeException(
                                        "Apenas coordenadores podem visualizar solicitações pendentes.");
                }

                return repository.findByStatus(
                                RequestStatus.PENDENTE)
                                .stream()
                                .map(mapper::toDTO)
                                .toList();
        }

        public List<PresentationRequestResponseDTO> listConfirmed() {

                return repository.findByStatus(
                                RequestStatus.CONFIRMADO)
                                .stream()
                                .map(mapper::toDTO)
                                .toList();
        }

        public List<PresentationRequestResponseDTO> listAll(UserInfo currentUser) {

                if (!isAdmin(currentUser)) {

                        throw new RuntimeException(
                                        "Apenas administradores podem visualizar todas as solicitações.");
                }

                return repository.findAll()
                                .stream()
                                .map(mapper::toDTO)
                                .toList();
        }

        public List<PresentationRequestResponseDTO> listMyRequests(UserInfo currentUser) {

                return repository
                                .findByRequesterId(
                                                currentUser.getId())
                                .stream()
                                .map(mapper::toDTO)
                                .toList();
        }

        public Page<PresentationRequestDTO> listConfirmedByEvent(Long eventId, Pageable pageable) {

                return repository
                                .findByEventIdAndStatus(
                                                eventId,
                                                RequestStatus.CONFIRMADO,
                                                pageable)
                                .map(r -> new PresentationRequestDTO(
                                                r.getId(),
                                                r.getEvent().getId(),
                                                r.getEvent().getTitle(),
                                                r.getEvent().getDateTime(),
                                                r.getMusicalGroup() != null ? r.getMusicalGroup().getId() : null,
                                                r.getMusicalGroup() != null ? r.getMusicalGroup().getNome() : null,
                                                r.getRequester().getId(),
                                                r.getRequester().getName(),
                                                PresentationRequestStatus.valueOf(r.getStatus().name()),
                                                r.getCancellationReason()));
        }

        public PresentationRequestResponseDTO createComDadosExtras(
                CreatePresentationRequestDTO dto, 
                Long groupId, 
                String time, 
                UserInfo user) {
                return this.create(dto, user); 
        }
}