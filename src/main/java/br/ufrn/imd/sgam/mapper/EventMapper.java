package br.ufrn.imd.sgam.mapper;

import br.ufrn.imd.sgam.dto.EventDTO;
import br.ufrn.imd.sgam.model.Event;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public Event toEntity(EventDTO dto) {
        if (dto == null) return null;
        Event event = new Event();
        event.setId(dto.id());
        event.setTitle(dto.title());
        event.setDateTime(dto.dateTime());
        event.setLocation(dto.location());
        event.setDescription(dto.description());
        return event;
    }

    public EventDTO toDTO(Event event) {
        if (event == null) return null;
        return new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDateTime(),
                event.getLocation(),
                event.getDescription()
        );
    }
}