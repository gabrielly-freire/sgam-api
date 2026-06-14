package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.CancelPresentationRequestDTO;
import br.ufrn.imd.sgam.dto.ConfirmPresentationRequestDTO;
import br.ufrn.imd.sgam.dto.CreatePresentationRequestDTO;
import br.ufrn.imd.sgam.dto.PresentationRequestResponseDTO;
import br.ufrn.imd.sgam.enums.RequestStatus;
import br.ufrn.imd.sgam.enums.Role;
import br.ufrn.imd.sgam.mapper.PresentationRequestMapper;
import br.ufrn.imd.sgam.model.Event;
import br.ufrn.imd.sgam.model.MusicalGroup;
import br.ufrn.imd.sgam.model.PresentationRequest;
import br.ufrn.imd.sgam.model.UserInfo;
import br.ufrn.imd.sgam.repository.EventRepository;
import br.ufrn.imd.sgam.repository.MusicalGroupRepository;
import br.ufrn.imd.sgam.repository.PresentationRequestRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

import static org.mockito.Mockito.*;

class PresentationRequestServiceTest {

    private PresentationRequestRepository repository;

    private EventRepository eventRepository;

    private MusicalGroupRepository musicalGroupRepository;

    private PresentationRequestMapper mapper;

    private PresentationRequestService service;

    @BeforeEach
    void setup() {

        repository =
                mock(PresentationRequestRepository.class);

        eventRepository =
                mock(EventRepository.class);

        musicalGroupRepository =
                mock(MusicalGroupRepository.class);

        mapper =
                mock(PresentationRequestMapper.class);

        service =
                new PresentationRequestService(
                        repository,
                        eventRepository,
                        musicalGroupRepository,
                        mapper
                );
    }

    @Test
    @DisplayName("Deve criar solicitação")
    void shouldCreateRequest() {

        Event event = new Event();
        event.setId(1L);

        UserInfo requester = new UserInfo();
        requester.setId(10L);

        CreatePresentationRequestDTO dto =
                new CreatePresentationRequestDTO(
                        1L
                );

        when(
                eventRepository.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(event)
        );

        when(
                repository.save(
                        any(PresentationRequest.class)
                )
        ).thenAnswer(
                invocation ->
                        invocation.getArgument(0)
        );

        when(
                mapper.toDTO(
                        any(PresentationRequest.class)
                )
        ).thenReturn(
                mock(PresentationRequestResponseDTO.class)
        );

        service.create(
                dto,
                requester
        );

        verify(repository)
                .save(
                        any(PresentationRequest.class)
                );
    }

    @Test
    @DisplayName("Deve listar solicitações pendentes")
    void shouldListPendingRequests() {

        UserInfo coordinator = new UserInfo();

        coordinator.setRole(
                Role.COORDENADOR
        );

        when(
                repository.findByStatus(
                        RequestStatus.PENDENTE
                )
        ).thenReturn(
                List.of(
                        new PresentationRequest()
                )
        );

        when(
                mapper.toDTO(
                        any()
                )
        ).thenReturn(
                mock(PresentationRequestResponseDTO.class)
        );

        service.listPending(
                coordinator
        );

        verify(repository)
                .findByStatus(
                        RequestStatus.PENDENTE
                );
    }

    @Test
    @DisplayName("Deve listar solicitações do usuário")
    void shouldListMyRequests() {

        UserInfo user = new UserInfo();
        user.setId(1L);

        when(
                repository.findByRequesterId(
                        1L
                )
        ).thenReturn(
                List.of(
                        new PresentationRequest()
                )
        );

        when(
                mapper.toDTO(any())
        ).thenReturn(
                mock(PresentationRequestResponseDTO.class)
        );

        service.listMyRequests(
                user
        );

        verify(repository)
                .findByRequesterId(
                        1L
                );
    }

    @Test
    @DisplayName("Deve listar todas solicitações")
    void shouldListAllRequests() {

        UserInfo admin = new UserInfo();

        admin.setRole(
                Role.ADMIN
        );

        when(
                repository.findAll()
        ).thenReturn(
                List.of(
                        new PresentationRequest()
                )
        );

        when(
                mapper.toDTO(any())
        ).thenReturn(
                mock(PresentationRequestResponseDTO.class)
        );

        service.listAll(
                admin
        );

        verify(repository)
                .findAll();
    }

    @Test
    @DisplayName("Deve confirmar solicitação")
    void shouldConfirmRequest() {

        UserInfo coordinator = new UserInfo();

        coordinator.setId(1L);

        Event event = new Event();
        event.setDateTime(
                LocalDateTime.now()
        );

        MusicalGroup group =
                new MusicalGroup();

        group.setId(2L);
        group.setCoordenador(
                coordinator
        );

        PresentationRequest request =
                new PresentationRequest();

        request.setEvent(
                event
        );

        when(
                repository.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(request)
        );

        when(
                musicalGroupRepository.findById(
                        2L
                )
        ).thenReturn(
                Optional.of(group)
        );

        when(
                repository.existsConfirmedPresentationAtSameTime(
                        eq(2L),
                        any(LocalDateTime.class)
                )
        ).thenReturn(
                false
        );

        ConfirmPresentationRequestDTO dto =
                new ConfirmPresentationRequestDTO(
                        2L
                );

        service.confirm(
                1L,
                dto,
                coordinator
        );

        verify(repository)
                .save(request);
    }

    @Test
    @DisplayName("Deve cancelar solicitação")
    void shouldCancelRequest() {

        UserInfo requester =
                new UserInfo();

        requester.setId(1L);

        PresentationRequest request =
                new PresentationRequest();

        request.setRequester(
                requester
        );

        when(
                repository.findById(
                        1L
                )
        ).thenReturn(
                Optional.of(request)
        );

        CancelPresentationRequestDTO dto =
                new CancelPresentationRequestDTO(
                        "Motivo teste"
                );

        service.cancel(
                1L,
                dto,
                requester
        );

        verify(repository)
                .save(request);
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for coordenador")
    void shouldThrowWhenUserIsNotCoordinator() {

        UserInfo user = new UserInfo();

        user.setRole(
                Role.ADMIN
        );

        org.junit.jupiter.api.Assertions
                .assertThrows(
                        RuntimeException.class,
                        () -> service.listPending(user)
                );
    }
}