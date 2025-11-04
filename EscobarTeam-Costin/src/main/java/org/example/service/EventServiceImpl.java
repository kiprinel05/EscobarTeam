package org.example.service;

import org.example.dto.*;
import org.example.entity.Event;
import org.example.entity.Stage;
import org.example.mapper.EventMapper;
import org.example.repository.EventRepository;
import org.example.repository.StageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;
    private final StageRepository stageRepository;
    private final EventMapper eventMapper;

    @Autowired
    public EventServiceImpl(EventRepository eventRepository, 
                           StageRepository stageRepository, 
                           EventMapper eventMapper) {
        this.eventRepository = eventRepository;
        this.stageRepository = stageRepository;
        this.eventMapper = eventMapper;
    }

    @Override
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenimentul cu ID " + id + " nu a fost gasit"));
        return eventMapper.toResponseDTO(event);
    }

    @Override
    public EventResponseDTO createEvent(EventCreateDTO eventCreateDTO) {
        Stage stage = stageRepository.findById(eventCreateDTO.getStageId())
                .orElseThrow(() -> new RuntimeException("Scena cu ID-ul " + eventCreateDTO.getStageId() + " nu a fost gasita"));
        
        List<Event> conflictingEvents = eventRepository.findConflictingEvents(
                eventCreateDTO.getStageId(), 
                eventCreateDTO.getDate(), 
                eventCreateDTO.getDate().plusHours(2)
        );
        
        if (!conflictingEvents.isEmpty()) {
            throw new RuntimeException("Scena nu este disponibila in data si ora specificata");
        }
        
        Event event = eventMapper.toEntity(eventCreateDTO, stage);
        Event savedEvent = eventRepository.save(event);
        return eventMapper.toResponseDTO(savedEvent);
    }

    @Override
    public EventResponseDTO updateEvent(Long id, EventDTO eventDTO) {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenimentul cu ID " + id + " nu a fost gasit"));
        
        Stage stage = stageRepository.findById(eventDTO.getStageId())
                .orElseThrow(() -> new RuntimeException("Scena cu ID-ul " + eventDTO.getStageId() + " nu a fost gasita"));
        
        if (!existingEvent.getDate().equals(eventDTO.getDate()) || 
            !existingEvent.getStage().getId().equals(eventDTO.getStageId())) {
            List<Event> conflictingEvents = eventRepository.findConflictingEvents(
                    eventDTO.getStageId(), 
                    eventDTO.getDate(), 
                    eventDTO.getDate().plusHours(2)
            ).stream()
                    .filter(e -> !e.getId().equals(id))
                    .collect(Collectors.toList());
            
            if (!conflictingEvents.isEmpty()) {
                throw new RuntimeException("Scena nu este disponibila in data si ora specificata");
            }
        }
        
        eventMapper.updateEntityFromDTO(eventDTO, existingEvent, stage);
        Event updatedEvent = eventRepository.save(existingEvent);
        return eventMapper.toResponseDTO(updatedEvent);
    }

    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Evenimentul cu ID " + id + " nu a fost gasit");
        }
        eventRepository.deleteById(id);
    }

    @Override
    public List<EventResponseDTO> searchByName(String name) {
        return eventRepository.findByNameContainingIgnoreCase(name).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDTO> filterByStage(Long stageId) {
        return eventRepository.findByStageId(stageId).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDTO> filterByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findByDateBetween(startDate, endDate).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDTO> filterBySpecificDate(LocalDate date) {
        LocalDateTime startOfDay = date.atTime(LocalTime.MIN);
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        return eventRepository.findByDateBetween(startOfDay, endOfDay).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDTO> filterByCapacity(Integer minCapacity) {
        return eventRepository.findByCapacityGreaterThanEqual(minCapacity).stream()
                .map(eventMapper::toResponseDTO)
                .sorted((e1, e2) -> e2.getCapacity().compareTo(e1.getCapacity()))
                .collect(Collectors.toList());
    }

    @Override
    public List<EventResponseDTO> sortByDate(String order) {
        List<EventResponseDTO> events = getAllEvents();
        if ("desc".equalsIgnoreCase(order)) {
            return events.stream()
                    .sorted((e1, e2) -> e2.getDate().compareTo(e1.getDate()))
                    .collect(Collectors.toList());
        }
        return events;
    }

    @Override
    public List<EventResponseDTO> sortByCapacity(String order) {
        List<EventResponseDTO> events = getAllEvents();
        Comparator<EventResponseDTO> comparator = Comparator.comparing(EventResponseDTO::getCapacity);
        if ("desc".equalsIgnoreCase(order)) {
            comparator = comparator.reversed();
        }
        return events.stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    @Override
    public List<StageAvailabilityDTO> checkStageAvailability(LocalDateTime date) {
        List<Stage> allStages = stageRepository.findAll();
        LocalDateTime endDate = date.plusHours(2);
        
        return allStages.stream()
                .map(stage -> {
                    List<Event> conflictingEvents = eventRepository.findConflictingEvents(
                            stage.getId(), date, endDate
                    );
                    
                    boolean available = conflictingEvents.isEmpty();
                    List<LocalDateTime> availableTimeSlots = new ArrayList<>();
                    
                    if (available) {
                        availableTimeSlots = generateAvailableTimeSlots(stage.getId(), date.toLocalDate());
                    }
                    
                    return new StageAvailabilityDTO(
                            stage.getId(),
                            stage.getName(),
                            available,
                            availableTimeSlots
                    );
                })
                .collect(Collectors.toList());
    }

    private List<LocalDateTime> generateAvailableTimeSlots(Long stageId, LocalDate date) {
        List<LocalDateTime> slots = new ArrayList<>();
        LocalDateTime startOfDay = date.atTime(LocalTime.of(9, 0));
        LocalDateTime endOfDay = date.atTime(LocalTime.of(23, 0));
        
        LocalDateTime current = startOfDay;
        while (current.plusHours(2).isBefore(endOfDay) || current.plusHours(2).equals(endOfDay)) {
            List<Event> conflicts = eventRepository.findConflictingEvents(
                    stageId, current, current.plusHours(2)
            );
            if (conflicts.isEmpty()) {
                slots.add(current);
            }
            current = current.plusHours(2);
        }
        
        return slots;
    }

    @Override
    public List<EventResponseDTO> getStageSchedule(Long stageId) {
        return eventRepository.findAllByStageIdOrderByDate(stageId).stream()
                .map(eventMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EventStatisticsDTO generateStatistics() {
        List<Event> allEvents = eventRepository.findAll();
        
        long totalEvents = allEvents.size();
        
        long totalParticipants = allEvents.stream()
                .mapToLong(Event::getCapacity)
                .sum();
        
        Map<LocalDate, Long> eventsPerDay = allEvents.stream()
                .collect(Collectors.groupingBy(
                        event -> event.getDate().toLocalDate(),
                        Collectors.counting()
                ));
        
        Map<LocalDate, Long> participantsPerDay = allEvents.stream()
                .collect(Collectors.groupingBy(
                        event -> event.getDate().toLocalDate(),
                        Collectors.summingLong(Event::getCapacity)
                ));
        
        return new EventStatisticsDTO(totalEvents, totalParticipants, eventsPerDay, participantsPerDay);
    }
}

