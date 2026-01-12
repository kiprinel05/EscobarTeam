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

@Service
@Transactional
public class TicketServiceImpl implements ITicketService {
    
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final EventServiceClient eventServiceClient;
    
    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper, EventServiceClient eventServiceClient) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
        this.eventServiceClient = eventServiceClient;
    }
    
    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll().stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        return ticketMapper.toDTO(ticket);
    }
    
    @Override
    public TicketDTO createTicket(TicketCreateDTO ticketCreateDTO) {
        Ticket ticket = ticketMapper.toEntity(ticketCreateDTO);
        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDTO(savedTicket);
    }
    
    @Override
    public TicketDTO updateTicket(Long id, TicketDTO ticketDTO) {
        Ticket existingTicket = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException(id));
        
        ticketMapper.updateEntityFromDTO(ticketDTO, existingTicket);
        Ticket updatedTicket = ticketRepository.save(existingTicket);
        return ticketMapper.toDTO(updatedTicket);
    }
    
    @Override
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new TicketNotFoundException(id);
        }
        ticketRepository.deleteById(id);
    }
    
    @Override
    public List<TicketDTO> getTicketsByFestival(String eventName) {
        return ticketRepository.findByEventName(eventName).stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public int getAvailableSeats(String eventName) {
        return ticketRepository.countByEventName(eventName);
    }
    
    @Override
    public Map<String, Double> getRevenueByFestival() {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getEventName,
                        Collectors.summingDouble(ticket -> ticket.getPrice() * ticket.getQuantity())
                ));
    }
    
    @Override
    public List<TicketDTO> getTicketsByType(String ticketType) {
        return ticketRepository.findByTicketType(ticketType).stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public Double getTotalRevenue() {
        return ticketRepository.findAll().stream()
                .mapToDouble(ticket -> ticket.getPrice() * ticket.getQuantity())
                .sum();
    }

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

    private Double adjustPriceByRegion(Double basePrice, String region) {
        if ("US".equalsIgnoreCase(region) || region.contains("US")) {
            return basePrice * 1.1;
        } else if ("EU-RO".equalsIgnoreCase(region) || region.contains("RO")) {
            return basePrice * 5.0;
        } else {
            return basePrice;
        }
    }

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
