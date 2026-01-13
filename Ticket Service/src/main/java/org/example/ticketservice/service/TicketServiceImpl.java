package org.example.ticketservice.service;

import org.example.ticketservice.client.EventServiceClient;
import org.example.ticketservice.dto.EventDetailsDTO;
import org.example.ticketservice.dto.TicketCreateDTO;
import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.dto.TicketWithEventDetailsDTO;
import org.example.ticketservice.exception.TicketNotFoundException;
import org.example.ticketservice.mapper.TicketMapper;
import org.example.ticketservice.model.Ticket;
import org.example.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for managing tickets in the festival management system.
 * Provides business logic for CRUD operations, ticket purchasing, revenue calculation, and event validation.
 *
 * @author EscobarTeam
 */
@Service
@Transactional
public class TicketServiceImpl implements ITicketService {
    
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final EventServiceClient eventServiceClient;
    
    /**
     * Constructs a new {@code TicketServiceImpl} with the required dependencies.
     *
     * @param ticketRepository the repository for ticket data access
     * @param ticketMapper the mapper for converting between entities and DTOs
     * @param eventServiceClient the Feign client for communicating with the Event Service
     */
    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper, EventServiceClient eventServiceClient) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.eventServiceClient = eventServiceClient;
    }
    
    /**
     * Retrieves all tickets from the database.
     *
     * @return a list of all {@code TicketDTO} objects representing all tickets
     */
    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Retrieves a ticket by its unique identifier.
     *
     * @param id the unique identifier of the ticket to retrieve
     * @return the {@code TicketDTO} object representing the ticket with the specified ID
     * @throws TicketNotFoundException if no ticket exists with the given ID
     */
    @Override
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        return ticketMapper.toDTO(ticket);
    }
    
    /**
     * Creates a new ticket in the system.
     *
     * @param ticketCreateDTO the DTO containing the data for the new ticket
     * @return the {@code TicketDTO} object representing the newly created ticket
     */
    @Override
    public TicketDTO createTicket(TicketCreateDTO ticketCreateDTO) {
        Ticket ticket = ticketMapper.toEntity(ticketCreateDTO);
        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDTO(savedTicket);
    }
    
    /**
     * Updates an existing ticket with new information.
     *
     * @param id the unique identifier of the ticket to update
     * @param ticketDTO the DTO containing the updated ticket information
     * @return the {@code TicketDTO} object representing the updated ticket
     * @throws TicketNotFoundException if no ticket exists with the given ID
     */
    @Override
    public TicketDTO updateTicket(Long id, TicketDTO ticketDTO) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        
        ticketMapper.updateEntityFromDTO(ticketDTO, existingTicket);
        Ticket updatedTicket = ticketRepository.save(existingTicket);
        return ticketMapper.toDTO(updatedTicket);
    }
    
    /**
     * Deletes a ticket from the system by its unique identifier.
     *
     * @param id the unique identifier of the ticket to delete
     * @throws TicketNotFoundException if no ticket exists with the given ID
     */
    @Override
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(id);
        }
        ticketRepository.deleteById(id);
    }
    
    /**
     * Retrieves all tickets for a specific event.
     *
     * @param eventName the name of the event to filter tickets by
     * @return a list of {@code TicketDTO} objects representing tickets for the specified event
     */
    @Override
    public List<TicketDTO> getTicketsByFestival(String eventName) {
        return ticketRepository.findByEventName(eventName).stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates the number of available seats for a specific event.
     * This is calculated based on the event capacity minus sold tickets.
     *
     * @param eventName the name of the event
     * @return the number of available seats for the event
     */
    @Override
    public int getAvailableSeats(String eventName) {
        return ticketRepository.countByEventName(eventName);
    }
    
    /**
     * Calculates the total revenue generated by each event.
     *
     * @return a map where keys are event names and values are the total revenue for each event
     */
    @Override
    public Map<String, Double> getRevenueByFestival() {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getEventName,
                        Collectors.summingDouble(ticket -> ticket.getPrice() * ticket.getQuantity())
                ));
    }
    
    /**
     * Filters tickets by their type (e.g., VIP, GENERAL, EARLY_BIRD).
     *
     * @param ticketType the type of tickets to filter by
     * @return a list of {@code TicketDTO} objects representing tickets of the specified type
     */
    @Override
    public List<TicketDTO> getTicketsByType(String ticketType) {
        return ticketRepository.findByTicketType(ticketType).stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * Calculates the total revenue from all tickets in the system.
     *
     * @return the total revenue as a {@code Double} value
     */
    @Override
    public Double getTotalRevenue() {
        return ticketRepository.findAll().stream()
                .mapToDouble(ticket -> ticket.getPrice() * ticket.getQuantity())
                .sum();
    }

    /**
     * Retrieves ticket information along with detailed event information.
     * Validates the event exists and provides localized validation messages.
     *
     * @param eventName the name of the event
     * @param language the language code for message localization (e.g., "ro-RO", "en-US")
     * @return a {@code TicketWithEventDetailsDTO} object containing ticket and event details
     * @throws RuntimeException if no tickets exist for the event or if the event is not found
     */
    @Override
    public TicketWithEventDetailsDTO getTicketWithEventDetails(String eventName, String language) {
        List<Ticket> tickets = ticketRepository.findByEventName(eventName);
        if (tickets.isEmpty()) {
            throw new RuntimeException("Nu exista bilete pentru evenimentul: " + eventName);
        }
        
        Ticket ticket = tickets.get(0);
        TicketDTO ticketDTO = ticketMapper.toDTO(ticket);
        
        List<EventDetailsDTO> events = eventServiceClient.searchEventsByName(eventName, "Gateway-Service");
        
        if (events == null || events.isEmpty()) {
            throw new RuntimeException("Evenimentul '" + eventName + "' nu a fost gasit in Event Service");
        }
        
        EventDetailsDTO eventDetails = events.stream()
                .filter(e -> e.getName().equalsIgnoreCase(eventName))
                .findFirst()
                .orElse(events.get(0));
        
        String validationMessage = getValidationMessage(language, eventName, eventDetails.getDate());
        
        TicketWithEventDetailsDTO dto = new TicketWithEventDetailsDTO();
        dto.setId(ticketDTO.getId());
        dto.setEventName(ticketDTO.getEventName());
        dto.setTicketType(ticketDTO.getTicketType());
        dto.setPrice(ticketDTO.getPrice());
        dto.setQuantity(ticketDTO.getQuantity());
        dto.setBuyerName(ticketDTO.getBuyerName());
        dto.setBuyerEmail(ticketDTO.getBuyerEmail());
        dto.setPurchaseDate(ticketDTO.getPurchaseDate());
        dto.setIsActive(ticketDTO.getIsActive());
        dto.setCreatedAt(ticketDTO.getCreatedAt());
        dto.setEventDate(eventDetails.getDate());
        dto.setStageName(eventDetails.getStageName());
        dto.setAssociatedArtist(eventDetails.getAssociatedArtist());
        dto.setEventCapacity(eventDetails.getCapacity());
        dto.setValidationMessage(validationMessage);
        
        return dto;
    }

    /**
     * Purchases a ticket with validation against the Event Service.
     * Validates that the event exists and adjusts pricing based on region.
     *
     * @param ticketCreateDTO the DTO containing the ticket purchase information
     * @param region the region code for pricing adjustment (e.g., "EU-RO", "US")
     * @param language the language code for message localization (e.g., "ro-RO", "en-US")
     * @return the {@code TicketDTO} object representing the purchased ticket
     * @throws RuntimeException if the event does not exist
     */
    @Override
    public TicketDTO purchaseTicketWithValidation(TicketCreateDTO ticketCreateDTO, String region, String language) {
        List<EventDetailsDTO> events = eventServiceClient.searchEventsByName(
                ticketCreateDTO.getEventName(), 
                "Gateway-Service"
        );
        
        if (events == null || events.isEmpty()) {
            throw new RuntimeException("Evenimentul '" + ticketCreateDTO.getEventName() + "' nu exista");
        }
        
        EventDetailsDTO eventDetails = events.stream()
                .filter(e -> e.getName().equalsIgnoreCase(ticketCreateDTO.getEventName()))
                .findFirst()
                .orElse(events.get(0));
        
        Double adjustedPrice = adjustPriceByRegion(ticketCreateDTO.getPrice(), region);
        ticketCreateDTO.setPrice(adjustedPrice);
        
        Ticket ticket = ticketMapper.toEntity(ticketCreateDTO);
        Ticket savedTicket = ticketRepository.save(ticket);
        
        return ticketMapper.toDTO(savedTicket);
    }

    /**
     * Adjusts ticket price based on the region.
     * US region: 10% markup, EU-RO region: 5x multiplier, others: no adjustment.
     *
     * @param basePrice the base price of the ticket
     * @param region the region code for pricing adjustment (e.g., "EU-RO", "US")
     * @return the adjusted price based on the region
     */
    private Double adjustPriceByRegion(Double basePrice, String region) {
        if ("US".equalsIgnoreCase(region) || region.contains("US")) {
            return basePrice * 1.1;
        } else if ("EU-RO".equalsIgnoreCase(region) || region.contains("RO")) {
            return basePrice * 5.0;
        } else {
            return basePrice;
        }
    }

    /**
     * Generates a localized validation message for ticket purchase.
     *
     * @param language the language code for message localization (e.g., "ro-RO", "en-US")
     * @param eventName the name of the event
     * @param eventDate the date and time of the event
     * @return a localized validation message string
     */
    private String getValidationMessage(String language, String eventName, LocalDateTime eventDate) {
        if ("en-US".equalsIgnoreCase(language) || "en".equalsIgnoreCase(language)) {
            return String.format("Ticket validated for event '%s' scheduled on %s.", 
                    eventName, eventDate != null ? eventDate.toString() : "TBD");
        } else {
            return String.format("Bilet validat pentru evenimentul '%s' programat pe %s.", 
                    eventName, eventDate != null ? eventDate.toString() : "TBD");
        }
    }
}
