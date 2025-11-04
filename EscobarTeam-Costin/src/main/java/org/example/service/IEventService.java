package org.example.service;

import org.example.dto.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface IEventService {
    List<EventResponseDTO> getAllEvents();
    EventResponseDTO getEventById(Long id);
    EventResponseDTO createEvent(EventCreateDTO eventCreateDTO);
    EventResponseDTO updateEvent(Long id, EventDTO eventDTO);
    void deleteEvent(Long id);
    
    List<EventResponseDTO> searchByName(String name);
    List<EventResponseDTO> searchByArtist(String artist);
    List<EventResponseDTO> filterByStage(Long stageId);
    List<EventResponseDTO> filterByDate(LocalDateTime startDate, LocalDateTime endDate);
    List<EventResponseDTO> filterBySpecificDate(LocalDate date);
    List<EventResponseDTO> filterByCapacity(Integer minCapacity);
    List<EventResponseDTO> sortByDate(String order);
    List<EventResponseDTO> sortByCapacity(String order);
    
    List<StageAvailabilityDTO> checkStageAvailability(LocalDateTime date);
    List<EventResponseDTO> getStageSchedule(Long stageId);
    EventStatisticsDTO generateStatistics();
}

