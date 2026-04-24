package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.EventDTO;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.EventMapper;
import br.ufrn.imd.sgam.model.Event;
import br.ufrn.imd.sgam.repository.EventRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private EventMapper eventMapper;

    @InjectMocks
    private EventService eventService;

    // --- TESTES DO MÉTODO SAVE ---

    @Test
    @DisplayName("SAVE: Deve salvar evento com sucesso (data futura)")
    void testShouldSaveEventSuccessfully() {
        EventDTO dto = createFutureDTO();
        Event model = new Event();

        when(eventMapper.toEntity(dto)).thenReturn(model);
        when(eventRepository.save(model)).thenReturn(model);
        when(eventMapper.toDTO(model)).thenReturn(dto);

        EventDTO result = eventService.save(dto);

        assertNotNull(result);
        verify(eventRepository).save(any());
    }

    @Test
    @DisplayName("SAVE: Deve falhar ao salvar evento em data passada")
    void testShouldFailSaveEventWithPastDate() {
        EventDTO dto = createPastDTO();

        assertThrows(BusinessException.class, () -> eventService.save(dto));
        verify(eventRepository, never()).save(any());
    }

    // --- TESTES DO MÉTODO GET ---

    @Test
    @DisplayName("GET: Deve retornar evento por ID")
    void testShouldGetEventById() {
        Long id = 1L;
        Event model = new Event();
        when(eventRepository.findById(id)).thenReturn(Optional.of(model));
        when(eventMapper.toDTO(model)).thenReturn(createFutureDTO());

        EventDTO result = eventService.get(id);

        assertNotNull(result);
    }

    @Test
    @DisplayName("GET: Deve falhar ao buscar ID inexistente")
    void testShouldFailGetEventById() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> eventService.get(1L));
    }

    // --- TESTES DO MÉTODO LIST (PAGINADO E ORDENADO) ---

    @Test
    @DisplayName("LIST: Deve retornar página de eventos ordenada cronologicamente")
    void testShouldGetEventsPageSortedByDate() {
        Pageable pageable = PageRequest.of(0, 10);
        Pageable expectedSortedPageable = PageRequest.of(0, 10, Sort.by("dateTime").ascending());
        
        Event model = new Event();
        Page<Event> page = new PageImpl<>(List.of(model));

        // Note que verificamos se o repositório é chamado com o pageable ordenado que criamos no Service
        when(eventRepository.findAllPage(expectedSortedPageable)).thenReturn(page);
        when(eventMapper.toDTO(model)).thenReturn(createFutureDTO());

        Page<EventDTO> result = eventService.list(pageable);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    // --- TESTES DO MÉTODO UPDATE ---

    @Test
    @DisplayName("UPDATE: Deve atualizar evento com sucesso (data futura)")
    void testShouldUpdateEventSuccessfully() {
        Long id = 1L;
        EventDTO dto = createFutureDTO();
        Event model = new Event();

        when(eventRepository.findById(id)).thenReturn(Optional.of(model));
        when(eventMapper.toEntity(dto)).thenReturn(model);
        when(eventRepository.save(model)).thenReturn(model);
        when(eventMapper.toDTO(model)).thenReturn(dto);

        EventDTO result = eventService.update(id, dto);

        assertNotNull(result);
        verify(eventRepository).save(model);
    }

    @Test
    @DisplayName("UPDATE: Deve falhar ao atualizar para data passada")
    void testShouldFailUpdateEventWithPastDate() {
        Long id = 1L;
        EventDTO dto = createPastDTO();
        Event model = new Event();

        when(eventRepository.findById(id)).thenReturn(Optional.of(model));

        assertThrows(BusinessException.class, () -> eventService.update(id, dto));
        verify(eventRepository, never()).save(any());
    }

    @Test
    @DisplayName("UPDATE: Deve falhar ao atualizar ID inexistente")
    void testShouldFailUpdateEventWithNonExistingId() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> eventService.update(1L, createFutureDTO()));
    }

    // --- TESTES DO MÉTODO DELETE ---

    @Test
    @DisplayName("DELETE: Deve deletar com sucesso")
    void testShouldDeleteEventSuccessfully() {
        Long id = 1L;
        when(eventRepository.findById(id)).thenReturn(Optional.of(new Event()));

        eventService.delete(id);

        verify(eventRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("DELETE: Deve falhar ao deletar ID inexistente")
    void testShouldFailDeleteEventWithNonExistingId() {
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> eventService.delete(1L));
    }

    // Helper methods to ensure deterministic test results regarding dates
    private EventDTO createFutureDTO() {
        return new EventDTO(1L, "Concerto", LocalDateTime.now().plusDays(10), "Auditório", "Descrição");
    }

    private EventDTO createPastDTO() {
        return new EventDTO(1L, "Concerto Passado", LocalDateTime.now().minusDays(10), "Auditório", "Descrição");
    }
}