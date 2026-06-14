package br.ufrn.imd.sgam.service;

import br.ufrn.imd.sgam.dto.EventDTO;
import br.ufrn.imd.sgam.exception.BusinessException;
import br.ufrn.imd.sgam.exception.ResourceNotFoundException;
import br.ufrn.imd.sgam.mapper.EventMapper;
import br.ufrn.imd.sgam.model.Event;
import br.ufrn.imd.sgam.repository.EventRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class EventService {

    private final EventRepository repository;
    private final EventMapper mapper;

    public EventDTO save(EventDTO dto) {
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Não é possível agendar eventos em datas passadas.", HttpStatus.BAD_REQUEST);
        }
        Event event = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(event));
    }

    public Page<EventDTO> list(Pageable pageable) {
        // Força a ordenação cronológica (pela data/hora)
        Pageable sortedByDate = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by("dateTime").ascending()
        );
        return repository.findAllPage(sortedByDate).map(mapper::toDTO);
    }

    public EventDTO get(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));
    }

    public EventDTO update(Long id, EventDTO dto) {
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));
        
        if (dto.dateTime().isBefore(LocalDateTime.now())) {
            throw new BusinessException("A nova data do evento não pode ser no passado.", HttpStatus.BAD_REQUEST);
        }

        Event event = mapper.toEntity(dto);
        event.setId(id);
        return mapper.toDTO(repository.save(event));
    }

    public void delete(Long id) {
        repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Evento não encontrado"));
        repository.deleteById(id);
    }
}