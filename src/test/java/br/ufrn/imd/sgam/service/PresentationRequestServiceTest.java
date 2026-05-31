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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Teste funcional do serviço de solicitação de apresentações - PresentationRequestService")
public class PresentationRequestServiceTest {

    @Mock
    private PresentationRequestRepository presentationRequestRepository;

    @Mock
    private EventRepository eventRepository;

    @Mock
    private MusicalGroupRepository musicalGroupRepository;

    @Mock
    private UserInfoRepository userInfoRepository;

    @Mock
    private PresentationRequestMapper presentationRequestMapper;

    @InjectMocks
    private PresentationRequestService presentationRequestService;

    @Nested
    @DisplayName("Casos de testes para solicitação de uma apresentação")
    class TestCasesForCreatePresentationRequest {
        
        @Test
        @DisplayName("Deve criar solicitacao pendente com sucesso")
        void testShouldCreatePendingPresentationRequestSuccessfully() {
            PresentationRequestCreateDTO createDTO = new PresentationRequestCreateDTO(1L, 2L);
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);
            PresentationRequestDTO expectedDTO = createDTO(PresentationRequestStatus.PENDENTE, null);

            when(eventRepository.findById(1L)).thenReturn(Optional.of(presentationRequest.getEvent()));
            when(musicalGroupRepository.findById(2L)).thenReturn(Optional.of(presentationRequest.getMusicalGroup()));
            when(userInfoRepository.findById(3L)).thenReturn(Optional.of(presentationRequest.getSolicitante()));
            when(presentationRequestRepository.save(any(PresentationRequest.class))).thenReturn(presentationRequest);
            when(presentationRequestMapper.toDTO(presentationRequest)).thenReturn(expectedDTO);

            PresentationRequestDTO result = presentationRequestService.save(createDTO, 3L);

            assertNotNull(result);
            assertEquals(PresentationRequestStatus.PENDENTE, result.status());
            verify(presentationRequestRepository).save(any(PresentationRequest.class));
        }

        @Test
        @DisplayName("Deve falhar quando evento nao existe")
        void testShouldFailCreateWhenEventDoesNotExist() {
            PresentationRequestCreateDTO createDTO = new PresentationRequestCreateDTO(1L, 2L);

            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
                    () -> presentationRequestService.save(createDTO, 3L));
            
            assertEquals("Evento não encontrado", ex.getMessage());
            verify(presentationRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve falhar quando grupo musical nao existe")
        void testShouldFailCreateWhenMusicalGroupDoesNotExist() {
            PresentationRequestCreateDTO createDTO = new PresentationRequestCreateDTO(1L, 2L);
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);

            when(eventRepository.findById(1L)).thenReturn(Optional.of(presentationRequest.getEvent()));
            when(musicalGroupRepository.findById(2L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
                    () -> presentationRequestService.save(createDTO, 3L));
            
            assertEquals("Grupo musical não encontrado", ex.getMessage());
            verify(presentationRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve falhar quando solicitante nao existe")
        void testShouldFailCreateWhenRequesterDoesNotExist() {
            PresentationRequestCreateDTO createDTO = new PresentationRequestCreateDTO(1L, 2L);
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);

            when(eventRepository.findById(1L)).thenReturn(Optional.of(presentationRequest.getEvent()));
            when(musicalGroupRepository.findById(2L)).thenReturn(Optional.of(presentationRequest.getMusicalGroup()));
            when(userInfoRepository.findById(3L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
                    () -> presentationRequestService.save(createDTO, 3L));
            assertEquals("Solicitante não encontrado", ex.getMessage());
            verify(presentationRequestRepository, never()).save(any());
        }

    }
    
    @Nested
    @DisplayName("Casos de testes para listagem de solicitacoes pendentes")
    class TestCasesForListPendingByCoordinator {
        @Test
        @DisplayName("Deve listar solicitacoes pendentes do coordenador")
        void testShouldListPendingRequestsByCoordinator() {
            Pageable pageable = PageRequest.of(0, 10);
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);
            Page<PresentationRequest> page = new PageImpl<>(List.of(presentationRequest));

            when(presentationRequestRepository.findByMusicalGroupCoordenadorIdAndStatus(
                    4L,
                    PresentationRequestStatus.PENDENTE,
                    pageable
            )).thenReturn(page);
            when(presentationRequestMapper.toDTO(presentationRequest)).thenReturn(createDTO(
                    PresentationRequestStatus.PENDENTE,
                    null
            ));

            Page<PresentationRequestDTO> result = presentationRequestService.listPendingByCoordinator(4L, pageable);

            assertFalse(result.isEmpty());
            assertEquals(1, result.getTotalElements());
        }
    }

    @Nested
    @DisplayName("Casos de testes para confirmação de solicitações de apresentações")
    class TestCasesForConfirmedPresentationRequest {
        @Test
        @DisplayName("Deve confirmar solicitacao pendente sem conflito de horario")
        void testShouldConfirmPendingRequestSuccessfully() {
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);
            PresentationRequestDTO expectedDTO = createDTO(PresentationRequestStatus.CONFIRMADO, null);

            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.of(presentationRequest));
            when(presentationRequestRepository.existsByMusicalGroupIdAndStatusAndEventDateTimeAndIdNot(
                    eq(2L),
                    eq(PresentationRequestStatus.CONFIRMADO),
                    any(LocalDateTime.class),
                    eq(1L)
            )).thenReturn(false);
            when(presentationRequestRepository.save(presentationRequest)).thenReturn(presentationRequest);
            when(presentationRequestMapper.toDTO(presentationRequest)).thenReturn(expectedDTO);

            PresentationRequestDTO result = presentationRequestService.confirm(1L, 4L);

            assertEquals(PresentationRequestStatus.CONFIRMADO, presentationRequest.getStatus());
            assertEquals(PresentationRequestStatus.CONFIRMADO, result.status());
            verify(presentationRequestRepository).save(presentationRequest);
        }

        @Test
        @DisplayName("Deve falhar quando grupo ja tem apresentacao no mesmo horario")
        void testShouldFailConfirmWhenGroupHasScheduleConflict() {
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);

            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.of(presentationRequest));
            when(presentationRequestRepository.existsByMusicalGroupIdAndStatusAndEventDateTimeAndIdNot(
                    eq(2L),
                    eq(PresentationRequestStatus.CONFIRMADO),
                    any(LocalDateTime.class),
                    eq(1L)
            )).thenReturn(true);

            BusinessException ex = assertThrows(BusinessException.class, 
                    () -> presentationRequestService.confirm(1L, 4L));

            assertEquals(HttpStatus.CONFLICT, ex.getHttpStatusCode());
            assertEquals("Grupo musical ja possui apresentação confirmada neste horario.", ex.getMessage());
            verify(presentationRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve falhar quando solicitacao nao existe")
        void testShouldFailConfirmWhenRequestDoesNotExist() {
            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, 
                    () -> presentationRequestService.confirm(1L, 4L));

            assertEquals("Solicitação de apresentação não encontrada", ex.getMessage());
            verify(presentationRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve falhar quando solicitacao nao esta pendente")
        void testShouldFailConfirmWhenRequestIsNotPending() {
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.CONFIRMADO);

            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.of(presentationRequest));

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> presentationRequestService.confirm(1L, 4L)
            );

            assertEquals("Apenas solicitações pendentes podem ser confirmadas.", ex.getMessage());
            assertEquals(HttpStatus.CONFLICT, ex.getHttpStatusCode());
            verify(presentationRequestRepository, never())
                    .existsByMusicalGroupIdAndStatusAndEventDateTimeAndIdNot(any(), any(), any(), any());
            verify(presentationRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve falhar quando usuario nao coordena o grupo")
        void testShouldFailConfirmWhenUserIsNotGroupCoordinator() {
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);

            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.of(presentationRequest));

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> presentationRequestService.confirm(1L, 9L)
            );

            assertEquals("Usuario não coordena o grupo musical informado.", ex.getMessage());
            assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatusCode());
            verify(presentationRequestRepository, never())
                    .existsByMusicalGroupIdAndStatusAndEventDateTimeAndIdNot(any(), any(), any(), any());
            verify(presentationRequestRepository, never()).save(any());
        }
    }
    
    @Nested
    @DisplayName("Casos de testes para cancelamento de solicitações de apresentações")
    class TestCasesForCancelledPresentationRequest {
        @Test
        @DisplayName("Deve cancelar solicitacao informando motivo")
        void testShouldCancelRequestWithReason() {
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);
            PresentationRequestCancelDTO cancelDTO = new PresentationRequestCancelDTO("Agenda indisponivel");
            PresentationRequestDTO expectedDTO = createDTO(PresentationRequestStatus.CANCELADO, "Agenda indisponivel");

            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.of(presentationRequest));
            when(presentationRequestRepository.save(presentationRequest)).thenReturn(presentationRequest);
            when(presentationRequestMapper.toDTO(presentationRequest)).thenReturn(expectedDTO);

            PresentationRequestDTO result = presentationRequestService.cancel(1L, cancelDTO, 3L);

            assertEquals(PresentationRequestStatus.CANCELADO, presentationRequest.getStatus());
            assertEquals("Agenda indisponivel", presentationRequest.getCancellationReason());
            assertEquals("Agenda indisponivel", result.cancellationReason());
        }

        @Test
        @DisplayName("Deve falhar quando solicitacao nao existe")
        void testShouldFailCancelWhenRequestDoesNotExist() {
            PresentationRequestCancelDTO cancelDTO = new PresentationRequestCancelDTO("Agenda indisponivel");

            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> presentationRequestService.cancel(1L, cancelDTO, 3L));

            assertEquals("Solicitação de apresentação não encontrada", ex.getMessage());
            verify(presentationRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve falhar quando usuario nao tem permissao")
        void testShouldFailCancelWhenUserHasNoPermission() {
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.PENDENTE);
            PresentationRequestCancelDTO cancelDTO = new PresentationRequestCancelDTO("Agenda indisponivel");

            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.of(presentationRequest));

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> presentationRequestService.cancel(1L, cancelDTO, 9L)
            );

            assertEquals("Usuario não pode cancelar esta solicitação.", ex.getMessage());
            assertEquals(HttpStatus.FORBIDDEN, ex.getHttpStatusCode());
            verify(presentationRequestRepository, never()).save(any());
        }

        @Test
        @DisplayName("Deve falhar quando solicitacao ja esta cancelada")
        void testShouldFailCancelWhenRequestIsAlreadyCanceled() {
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.CANCELADO);
            PresentationRequestCancelDTO cancelDTO = new PresentationRequestCancelDTO("Agenda indisponivel");

            when(presentationRequestRepository.findById(1L)).thenReturn(Optional.of(presentationRequest));

            BusinessException ex = assertThrows(
                    BusinessException.class,
                    () -> presentationRequestService.cancel(1L, cancelDTO, 3L)
            );

            assertEquals("Solicitação de apresentação ja cancelada.", ex.getMessage());
            verify(presentationRequestRepository, never()).save(any());
        }

    }

    @Nested
    @DisplayName("Casos de testes para listagem de solicitacoes confirmadas")
    class TestCasesForListConfirmedRequestsByEvent {
        @Test
        @DisplayName("Deve listar apresentacoes confirmadas do evento")
        void testShouldListConfirmedRequestsByEvent() {
            Pageable pageable = PageRequest.of(0, 10);
            PresentationRequest presentationRequest = createPresentationRequest(PresentationRequestStatus.CONFIRMADO);
            Page<PresentationRequest> page = new PageImpl<>(List.of(presentationRequest));

            when(eventRepository.findById(1L)).thenReturn(Optional.of(presentationRequest.getEvent()));
            when(presentationRequestRepository.findByEventIdAndStatus(
                    1L,
                    PresentationRequestStatus.CONFIRMADO,
                    pageable
            )).thenReturn(page);
            when(presentationRequestMapper.toDTO(presentationRequest)).thenReturn(createDTO(
                    PresentationRequestStatus.CONFIRMADO,
                    null
            ));

            Page<PresentationRequestDTO> result = presentationRequestService.listConfirmedByEvent(1L, pageable);

            assertFalse(result.isEmpty());
            assertEquals(1, result.getTotalElements());
        }

        @Test
        @DisplayName("Deve falhar quando evento nao existe")
        void testShouldFailListConfirmedRequestsWhenEventDoesNotExist() {
            Pageable pageable = PageRequest.of(0, 10);

            when(eventRepository.findById(1L)).thenReturn(Optional.empty());

            ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                    () -> presentationRequestService.listConfirmedByEvent(1L, pageable));

            assertEquals("Evento não encontrado", ex.getMessage());
            verify(presentationRequestRepository, never()).findByEventIdAndStatus(any(), any(), any());
        }

    }
    

    private PresentationRequest createPresentationRequest(PresentationRequestStatus status) {
        UserInfo solicitante = new UserInfo();
        solicitante.setId(3L);
        solicitante.setName("Produtor");

        UserInfo coordenador = new UserInfo();
        coordenador.setId(4L);
        coordenador.setName("Coordenador");

        Event event = new Event();
        event.setId(1L);
        event.setTitle("Festival");
        event.setDateTime(LocalDateTime.now().plusDays(10));

        MusicalGroup musicalGroup = new MusicalGroup();
        musicalGroup.setId(2L);
        musicalGroup.setNome("Grupo Musical");
        musicalGroup.setCoordenador(coordenador);

        PresentationRequest presentationRequest = new PresentationRequest();
        presentationRequest.setId(1L);
        presentationRequest.setEvent(event);
        presentationRequest.setMusicalGroup(musicalGroup);
        presentationRequest.setSolicitante(solicitante);
        presentationRequest.setStatus(status);
        return presentationRequest;
    }

    private PresentationRequestDTO createDTO(PresentationRequestStatus status, String cancellationReason) {
        return new PresentationRequestDTO(
                1L,
                1L,
                "Festival",
                LocalDateTime.now().plusDays(10),
                2L,
                "Grupo Musical",
                3L,
                "Produtor",
                status,
                cancellationReason
        );
    }
}
