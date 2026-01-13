package org.example.service;

import org.example.client.TicketServiceClient;
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

/**
 * Service implementation for managing events in the festival management system.
 * Provides business logic for CRUD operations, searching, filtering, scheduling, and ticket management.
 *
 * @author EscobarTeam
 */
@Service
@Transactional
public class EventServiceImpl implements IEventService {

    private final EventRepository eventRepository;
    private final StageRepository stageRepository;
    private final EventMapper eventMapper;
    private final TicketServiceClient ticketServiceClient;

    /**
     * Constructs a new {@code EventServiceImpl} with the required dependencies.
     *
     * @param eventRepository the repository for event data access
     * @param stageRepository the repository for stage data access
     * @param eventMapper the mapper for converting between entities and DTOs
     * @param ticketServiceClient the Feign client for communicating with the Ticket Service
     */
    @Autowired
    public EventServiceImpl(EventRepository eventRepository, 
                           StageRepository stageRepository, 
                           EventMapper eventMapper,
                           TicketServiceClient ticketServiceClient) {
        this.eventRepository = eventRepository;
        this.stageRepository = stageRepository;
        this.eventMapper = eventMapper;
        this.ticketServiceClient = ticketServiceClient;
    }

    /**
     * Retrieves all events from the database, sorted by date.
     *
     * @return a list of all {@code EventResponseDTO} objects representing all events
     */
    @Override
    public List<EventResponseDTO> getAllEvents() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the unique identifier of the event to retrieve
     * @return the {@code EventResponseDTO} object representing the event with the specified ID
     * @throws RuntimeException if no event exists with the given ID
     */
    @Override
    public EventResponseDTO getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenimentul cu ID " + id + " nu a fost gasit"));
        return eventMapper.toResponseDTO(event);
    }

    /**
     * Creates a new event in the system.
     * Validates stage availability and prevents scheduling conflicts.
     *
     * @param eventCreateDTO the DTO containing the data for the new event
     * @return the {@code EventResponseDTO} object representing the newly created event
     * @throws RuntimeException if the stage does not exist or is not available at the specified time
     */
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

    /**
     * Updates an existing event with new information.
     * Validates stage availability if the date or stage is changed.
     *
     * @param id the unique identifier of the event to update
     * @param eventDTO the DTO containing the updated event information
     * @return the {@code EventResponseDTO} object representing the updated event
     * @throws RuntimeException if the event or stage does not exist, or if the stage is not available
     */
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

    /**
     * Deletes an event from the system by its unique identifier.
     *
     * @param id the unique identifier of the event to delete
     * @throws RuntimeException if no event exists with the given ID
     */
    @Override
    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException("Evenimentul cu ID " + id + " nu a fost gasit");
        }
        eventRepository.deleteById(id);
    }

    /**
     * Searches for events whose names contain the specified search string.
     * The search is case-insensitive and results are sorted by date.
     *
     * @param name the search string to match against event names
     * @return a list of {@code EventResponseDTO} objects matching the search criteria
     */
    @Override
    public List<EventResponseDTO> searchByName(String name) {
        return eventRepository.findByNameContainingIgnoreCase(name).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Filters events by the stage on which they are held.
     *
     * @param stageId the unique identifier of the stage to filter by
     * @return a list of {@code EventResponseDTO} objects representing events on the specified stage
     */
    @Override
    public List<EventResponseDTO> filterByStage(Long stageId) {
        return eventRepository.findByStageId(stageId).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Filters events that occur within a specified date range.
     *
     * @param startDate the start date and time of the range (inclusive)
     * @param endDate the end date and time of the range (inclusive)
     * @return a list of {@code EventResponseDTO} objects representing events within the date range
     */
    @Override
    public List<EventResponseDTO> filterByDate(LocalDateTime startDate, LocalDateTime endDate) {
        return eventRepository.findByDateBetween(startDate, endDate).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Filters events that occur on a specific date.
     *
     * @param date the date to filter by
     * @return a list of {@code EventResponseDTO} objects representing events on the specified date
     */
    @Override
    public List<EventResponseDTO> filterBySpecificDate(LocalDate date) {
        LocalDateTime startOfDay = date.atTime(LocalTime.MIN);
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);
        
        return eventRepository.findByDateBetween(startOfDay, endOfDay).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Filters events by minimum capacity threshold.
     *
     * @param minCapacity the minimum capacity value to filter by
     * @return a list of {@code EventResponseDTO} objects representing events with capacity greater than or equal to the specified value
     */
    @Override
    public List<EventResponseDTO> filterByCapacity(Integer minCapacity) {
        return eventRepository.findByCapacityGreaterThanEqual(minCapacity).stream()
                .map(eventMapper::toResponseDTO)
                .sorted((e1, e2) -> e2.getCapacity().compareTo(e1.getCapacity()))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all events sorted by date.
     *
     * @param order the sort order, either "asc" for ascending or "desc" for descending (defaults to ascending)
     * @return a list of {@code EventResponseDTO} objects sorted by date
     */
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

    /**
     * Retrieves all events sorted by capacity.
     *
     * @param order the sort order, either "asc" for ascending or "desc" for descending (defaults to ascending)
     * @return a list of {@code EventResponseDTO} objects sorted by capacity
     */
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

    /**
     * Checks the availability of all stages at a specific date and time.
     * Returns available time slots for each stage.
     *
     * @param date the date and time to check availability for
     * @return a list of {@code StageAvailabilityDTO} objects containing availability information for each stage
     */
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

    /**
     * Generates available time slots for a stage on a specific date.
     * Time slots are 2-hour intervals between 9:00 AM and 11:00 PM.
     *
     * @param stageId the unique identifier of the stage
     * @param date the date to generate time slots for
     * @return a list of {@code LocalDateTime} objects representing available time slots
     */
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

    /**
     * Retrieves the complete schedule of events for a specific stage.
     *
     * @param stageId the unique identifier of the stage
     * @return a list of {@code EventResponseDTO} objects representing all events scheduled for the stage
     */
    @Override
    public List<EventResponseDTO> getStageSchedule(Long stageId) {
        return eventRepository.findAllByStageIdOrderByDate(stageId).stream()
                .map(eventMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Generates statistics about all events in the system.
     * Includes total events, total participants, and daily breakdowns.
     *
     * @return an {@code EventStatisticsDTO} object containing event statistics
     */
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

    /**
     * Filters events by the associated artist name.
     * The search is case-insensitive and results are sorted by date.
     *
     * @param artist the artist name to filter by
     * @return a list of {@code EventResponseDTO} objects representing events associated with the specified artist
     */
    @Override
    public List<EventResponseDTO> filterByArtist(String artist) {
        return eventRepository.findByAssociatedArtistContainingIgnoreCase(artist).stream()
                .map(eventMapper::toResponseDTO)
                .sorted(Comparator.comparing(EventResponseDTO::getDate))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an event along with ticket information including available seats and revenue.
     *
     * @param id the unique identifier of the event
     * @param region the region code for localization and pricing (e.g., "EU-RO", "US")
     * @return an {@code EventWithTicketInfoDTO} object containing event and ticket information
     * @throws RuntimeException if no event exists with the given ID
     */
    @Override
    public EventWithTicketInfoDTO getEventWithTicketInfo(Long id, String region) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenimentul cu ID " + id + " nu a fost gasit"));
        
        EventResponseDTO eventDTO = eventMapper.toResponseDTO(event);
        
        Integer availableSeats = ticketServiceClient.getAvailableSeats(event.getName(), "Gateway-Service");
        Map<String, Double> revenueMap = ticketServiceClient.getRevenueByFestival("Gateway-Service");
        Double totalRevenue = revenueMap.getOrDefault(event.getName(), 0.0);
        
        String ticketStatus;
        if (availableSeats == null || availableSeats <= 0) {
            ticketStatus = "SOLD_OUT";
        } else if (availableSeats < event.getCapacity() * 0.1) {
            ticketStatus = "LIMITED";
        } else {
            ticketStatus = "AVAILABLE";
        }
        
        String message = getPriceMessage(region, event.getName(), totalRevenue);
        
        EventWithTicketInfoDTO dto = new EventWithTicketInfoDTO();
        dto.setId(eventDTO.getId());
        dto.setName(eventDTO.getName());
        dto.setDate(eventDTO.getDate());
        dto.setStageId(eventDTO.getStageId());
        dto.setStageName(eventDTO.getStageName());
        dto.setAssociatedArtist(eventDTO.getAssociatedArtist());
        dto.setCapacity(eventDTO.getCapacity());
        dto.setCreatedAt(eventDTO.getCreatedAt());
        dto.setAvailableSeats(availableSeats != null ? availableSeats : 0);
        dto.setTotalRevenue(totalRevenue);
        dto.setTicketStatus(ticketStatus);
        dto.setMessage(message);
        
        return dto;
    }

    /**
     * Reserves tickets for a specific event.
     * Validates ticket availability before reservation.
     *
     * @param id the unique identifier of the event
     * @param quantity the number of tickets to reserve
     * @param ticketType the type of tickets to reserve (e.g., "VIP", "GENERAL", "EARLY_BIRD")
     * @param region the region code for localization and pricing (e.g., "EU-RO", "US")
     * @return an {@code EventWithTicketInfoDTO} object containing updated event and ticket information
     * @throws RuntimeException if the event does not exist or if there are insufficient tickets available
     */
    @Override
    public EventWithTicketInfoDTO reserveTicketsForEvent(Long id, Integer quantity, String ticketType, String region) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evenimentul cu ID " + id + " nu a fost gasit"));
        
        Integer availableSeats = ticketServiceClient.getAvailableSeats(event.getName(), "Gateway-Service");
        
        if (availableSeats == null || availableSeats < quantity) {
            throw new RuntimeException("Nu sunt suficiente bilete disponibile. Disponibile: " + 
                    (availableSeats != null ? availableSeats : 0) + ", Solicitate: " + quantity);
        }
        
        return getEventWithTicketInfo(id, region);
    }

    /**
     * Generates a localized price message based on the region.
     *
     * @param region the region code for localization (e.g., "EU-RO", "US")
     * @param eventName the name of the event
     * @param revenue the total revenue generated by the event
     * @return a localized message string about the event revenue
     */
    private String getPriceMessage(String region, String eventName, Double revenue) {
        if ("US".equalsIgnoreCase(region) || region.contains("US")) {
            return String.format("Event '%s' has generated $%.2f in revenue (US pricing).", eventName, revenue);
        } else if ("EU-RO".equalsIgnoreCase(region) || region.contains("RO")) {
            return String.format("Evenimentul '%s' a generat %.2f RON în venituri (preturi România).", eventName, revenue);
        } else {
            return String.format("Event '%s' has generated %.2f EUR in revenue (EU pricing).", eventName, revenue);
        }
    }
}

