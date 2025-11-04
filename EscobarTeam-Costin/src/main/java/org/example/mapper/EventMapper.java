package org.example.mapper;


import org.example.dto.EventCreateDTO;
import org.example.dto.EventDTO;
import org.example.dto.EventResponseDTO;
import org.example.entity.Event;
import org.example.entity.Stage;
import org.springframework.stereotype.Component;

@Component
public class EventMapper {

    public EventResponseDTO toResponseDTO(Event event) {
        if (event == null) {
            return null;
        }
        EventResponseDTO dto = new EventResponseDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        dto.setStageId(event.getStage().getId());
        dto.setStageName(event.getStage().getName());
        dto.setAssociatedArtist(event.getAssociatedArtist());
        dto.setCapacity(event.getCapacity());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }

    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }
        EventDTO dto = new EventDTO();
        dto.setId(event.getId());
        dto.setName(event.getName());
        dto.setDate(event.getDate());
        dto.setStageId(event.getStage().getId());
        dto.setAssociatedArtist(event.getAssociatedArtist());
        dto.setCapacity(event.getCapacity());
        dto.setCreatedAt(event.getCreatedAt());
        return dto;
    }

    public Event toEntity(EventCreateDTO eventCreateDTO, Stage stage) {
        if (eventCreateDTO == null) {
            return null;
        }
        Event event = new Event();
        event.setName(eventCreateDTO.getName());
        event.setDate(eventCreateDTO.getDate());
        event.setStage(stage);
        event.setAssociatedArtist(eventCreateDTO.getAssociatedArtist());
        event.setCapacity(eventCreateDTO.getCapacity());
        return event;
    }

    public Event toEntity(EventDTO eventDTO, Stage stage) {
        if (eventDTO == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventDTO.getId());
        event.setName(eventDTO.getName());
        event.setDate(eventDTO.getDate());
        event.setStage(stage);
        event.setAssociatedArtist(eventDTO.getAssociatedArtist());
        event.setCapacity(eventDTO.getCapacity());
        event.setCreatedAt(eventDTO.getCreatedAt());
        return event;
    }

    public void updateEntityFromDTO(EventDTO eventDTO, Event event, Stage stage) {
        if (eventDTO == null || event == null) {
            return;
        }
        if (eventDTO.getName() != null) {
            event.setName(eventDTO.getName());
        }
        if (eventDTO.getDate() != null) {
            event.setDate(eventDTO.getDate());
        }
        if (stage != null) {
            event.setStage(stage);
        }
        if (eventDTO.getAssociatedArtist() != null) {
            event.setAssociatedArtist(eventDTO.getAssociatedArtist());
        }
        if (eventDTO.getCapacity() != null) {
            event.setCapacity(eventDTO.getCapacity());
        }
    }
}
