package org.example.service;

import org.example.dto.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for managing events in the festival management system.
 * Provides operations for CRUD operations, searching, filtering, scheduling, and ticket management.
 *
 * @author EscobarTeam
 */
public interface IEventService {
    /**
     * Retrieves all events from the database, sorted by date.
     *
     * @return a list of all {@code EventResponseDTO} objects representing all events
     */
    List<EventResponseDTO> getAllEvents();
    
    /**
     * Retrieves an event by its unique identifier.
     *
     * @param id the unique identifier of the event to retrieve
     * @return the {@code EventResponseDTO} object representing the event with the specified ID
     * @throws RuntimeException if no event exists with the given ID
     */
    EventResponseDTO getEventById(Long id);
    
    /**
     * Creates a new event in the system.
     * Validates stage availability and prevents scheduling conflicts.
     *
     * @param eventCreateDTO the DTO containing the data for the new event
     * @return the {@code EventResponseDTO} object representing the newly created event
     * @throws RuntimeException if the stage does not exist or is not available at the specified time
     */
    EventResponseDTO createEvent(EventCreateDTO eventCreateDTO);
    
    /**
     * Updates an existing event with new information.
     * Validates stage availability if the date or stage is changed.
     *
     * @param id the unique identifier of the event to update
     * @param eventDTO the DTO containing the updated event information
     * @return the {@code EventResponseDTO} object representing the updated event
     * @throws RuntimeException if the event or stage does not exist, or if the stage is not available
     */
    EventResponseDTO updateEvent(Long id, EventDTO eventDTO);
    
    /**
     * Deletes an event from the system by its unique identifier.
     *
     * @param id the unique identifier of the event to delete
     * @throws RuntimeException if no event exists with the given ID
     */
    void deleteEvent(Long id);
    
    /**
     * Searches for events whose names contain the specified search string.
     * The search is case-insensitive and results are sorted by date.
     *
     * @param name the search string to match against event names
     * @return a list of {@code EventResponseDTO} objects matching the search criteria
     */
    List<EventResponseDTO> searchByName(String name);
    
    /**
     * Filters events by the stage on which they are held.
     *
     * @param stageId the unique identifier of the stage to filter by
     * @return a list of {@code EventResponseDTO} objects representing events on the specified stage
     */
    List<EventResponseDTO> filterByStage(Long stageId);
    
    /**
     * Filters events that occur within a specified date range.
     *
     * @param startDate the start date and time of the range (inclusive)
     * @param endDate the end date and time of the range (inclusive)
     * @return a list of {@code EventResponseDTO} objects representing events within the date range
     */
    List<EventResponseDTO> filterByDate(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Filters events that occur on a specific date.
     *
     * @param date the date to filter by
     * @return a list of {@code EventResponseDTO} objects representing events on the specified date
     */
    List<EventResponseDTO> filterBySpecificDate(LocalDate date);
    
    /**
     * Filters events by minimum capacity threshold.
     *
     * @param minCapacity the minimum capacity value to filter by
     * @return a list of {@code EventResponseDTO} objects representing events with capacity greater than or equal to the specified value
     */
    List<EventResponseDTO> filterByCapacity(Integer minCapacity);
    
    /**
     * Retrieves all events sorted by date.
     *
     * @param order the sort order, either "asc" for ascending or "desc" for descending (defaults to ascending)
     * @return a list of {@code EventResponseDTO} objects sorted by date
     */
    List<EventResponseDTO> sortByDate(String order);
    
    /**
     * Retrieves all events sorted by capacity.
     *
     * @param order the sort order, either "asc" for ascending or "desc" for descending (defaults to ascending)
     * @return a list of {@code EventResponseDTO} objects sorted by capacity
     */
    List<EventResponseDTO> sortByCapacity(String order);
    
    /**
     * Checks the availability of all stages at a specific date and time.
     * Returns available time slots for each stage.
     *
     * @param date the date and time to check availability for
     * @return a list of {@code StageAvailabilityDTO} objects containing availability information for each stage
     */
    List<StageAvailabilityDTO> checkStageAvailability(LocalDateTime date);
    
    /**
     * Retrieves the complete schedule of events for a specific stage.
     *
     * @param stageId the unique identifier of the stage
     * @return a list of {@code EventResponseDTO} objects representing all events scheduled for the stage
     */
    List<EventResponseDTO> getStageSchedule(Long stageId);
    
    /**
     * Generates statistics about all events in the system.
     * Includes total events, total participants, and daily breakdowns.
     *
     * @return an {@code EventStatisticsDTO} object containing event statistics
     */
    EventStatisticsDTO generateStatistics();
    
    /**
     * Filters events by the associated artist name.
     * The search is case-insensitive and results are sorted by date.
     *
     * @param artist the artist name to filter by
     * @return a list of {@code EventResponseDTO} objects representing events associated with the specified artist
     */
    List<EventResponseDTO> filterByArtist(String artist);
    
    /**
     * Retrieves an event along with ticket information including available seats and revenue.
     *
     * @param id the unique identifier of the event
     * @param region the region code for localization and pricing (e.g., "EU-RO", "US")
     * @return an {@code EventWithTicketInfoDTO} object containing event and ticket information
     * @throws RuntimeException if no event exists with the given ID
     */
    EventWithTicketInfoDTO getEventWithTicketInfo(Long id, String region);
    
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
    EventWithTicketInfoDTO reserveTicketsForEvent(Long id, Integer quantity, String ticketType, String region);
}

