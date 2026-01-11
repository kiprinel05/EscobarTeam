package org.example.ticketservice.service;

import org.example.ticketservice.dto.TicketCreateDTO;
import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.dto.TicketStatisticsDTO;
import org.example.ticketservice.dto.TicketValidationDTO;
import org.example.ticketservice.dto.TicketWithEventDetailsDTO;
import org.example.ticketservice.exception.TicketNotFoundException;
import org.example.ticketservice.mapper.TicketMapper;
import org.example.ticketservice.model.Ticket;
import org.example.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class TicketServiceImpl implements ITicketService {
    
    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;
    private final org.example.ticketservice.client.EventServiceClient eventServiceClient;
    
    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper, 
                             org.example.ticketservice.client.EventServiceClient eventServiceClient) {
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
        try {
            org.example.ticketservice.client.EventServiceClient.EventResponseDTO event = 
                    eventServiceClient.getEventByName(eventName);
            
            if (event == null || event.getCapacity() == null) {
                return Integer.MAX_VALUE;
            }
            
            int soldTickets = ticketRepository.findByEventName(eventName).stream()
                    .mapToInt(Ticket::getQuantity)
                    .sum();
            
            int available = event.getCapacity() - soldTickets;
            return Math.max(0, available);
        } catch (Exception e) {
            return Integer.MAX_VALUE;
        }
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
    public TicketWithEventDetailsDTO getTicketWithEventDetails(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new TicketNotFoundException(ticketId));
        
        TicketWithEventDetailsDTO result = new TicketWithEventDetailsDTO();
        result.setTicketId(ticket.getId());
        result.setEventName(ticket.getEventName());
        result.setTicketType(ticket.getTicketType());
        result.setPrice(ticket.getPrice());
        result.setQuantity(ticket.getQuantity());
        result.setTotalPrice(ticket.getPrice() * ticket.getQuantity());
        
        try {
            org.example.ticketservice.client.EventServiceClient.EventResponseDTO event = 
                    eventServiceClient.getEventByName(ticket.getEventName());
            
            if (event != null) {
                result.setEventDate(event.getDate());
                result.setStageName(event.getStageName());
                result.setAssociatedArtist(event.getAssociatedArtist());
                result.setEventCapacity(event.getCapacity());
            }
        } catch (Exception e) {
        }
        
        return result;
    }

    @Override
    public TicketWithEventDetailsDTO validateAndCreateTicket(TicketValidationDTO validationDTO) {
        org.example.ticketservice.client.EventServiceClient.EventResponseDTO event = 
                eventServiceClient.getEventByName(validationDTO.getEventName());
        
        if (event == null) {
            throw new RuntimeException("Evenimentul cu numele '" + validationDTO.getEventName() + "' nu a fost găsit");
        }
        
        int availableSeats = getAvailableSeats(validationDTO.getEventName());
        if (availableSeats < validationDTO.getNumberOfSeats()) {
            throw new RuntimeException("Nu sunt suficiente locuri disponibile. Disponibile: " + availableSeats);
        }
        
        TicketCreateDTO ticketCreateDTO = new TicketCreateDTO();
        ticketCreateDTO.setEventName(validationDTO.getEventName());
        ticketCreateDTO.setTicketType(validationDTO.getTicketType() != null ? validationDTO.getTicketType() : "Standard");
        ticketCreateDTO.setPrice(50.0);
        ticketCreateDTO.setQuantity(validationDTO.getNumberOfSeats());
        
        TicketDTO createdTicket = createTicket(ticketCreateDTO);
        
        TicketWithEventDetailsDTO result = new TicketWithEventDetailsDTO();
        result.setTicketId(createdTicket.getId());
        result.setEventName(createdTicket.getEventName());
        result.setEventDate(event.getDate());
        result.setStageName(event.getStageName());
        result.setAssociatedArtist(event.getAssociatedArtist());
        result.setEventCapacity(event.getCapacity());
        result.setTicketType(createdTicket.getTicketType());
        result.setPrice(createdTicket.getPrice());
        result.setQuantity(createdTicket.getQuantity());
        result.setTotalPrice(createdTicket.getPrice() * createdTicket.getQuantity());
        
        return result;
    }

    @Override
    public TicketStatisticsDTO getTicketStatisticsByEventAndStage(String eventName, String stageName, String ticketType) {
        org.example.ticketservice.client.EventServiceClient.EventResponseDTO event = null;
        try {
            event = eventServiceClient.getEventByName(eventName);
        } catch (Exception e) {
            throw new RuntimeException("Eroare la comunicarea cu Event Service: " + e.getMessage());
        }
        
        if (event == null) {
            try {
                List<org.example.ticketservice.client.EventServiceClient.EventResponseDTO> allEvents = 
                        eventServiceClient.getAllEvents();
                if (allEvents != null && !allEvents.isEmpty()) {
                    String availableEvents = allEvents.stream()
                            .map(e -> e.getName())
                            .filter(n -> n != null)
                            .limit(5)
                            .collect(Collectors.joining(", "));
                    throw new RuntimeException("Evenimentul cu numele '" + eventName + "' nu a fost găsit. " +
                            "Evenimente disponibile: " + availableEvents);
                }
            } catch (Exception ex) {
            }
            throw new RuntimeException("Evenimentul cu numele '" + eventName + "' nu a fost găsit. " +
                    "Verifică că numele este corect și că evenimentul există în baza de date.");
        }
        
        if (!event.getStageName().equalsIgnoreCase(stageName)) {
            throw new RuntimeException("Evenimentul '" + eventName + "' nu are loc pe scena '" + stageName + "'. Scena corectă este: " + event.getStageName());
        }
        
        List<Ticket> allTickets = ticketRepository.findByEventName(eventName);
        
        List<Ticket> filteredTickets = allTickets.stream()
                .filter(t -> t.getTicketType().equalsIgnoreCase(ticketType))
                .collect(Collectors.toList());
        
        int numberOfTickets = filteredTickets.size();
        int totalSeats = filteredTickets.stream()
                .mapToInt(Ticket::getQuantity)
                .sum();
        double totalRevenue = filteredTickets.stream()
                .mapToDouble(t -> t.getPrice() * t.getQuantity())
                .sum();
        
        TicketStatisticsDTO result = new TicketStatisticsDTO();
        result.setEventName(eventName);
        result.setStageName(stageName);
        result.setTicketType(ticketType);
        result.setNumberOfTickets(numberOfTickets);
        result.setTotalSeats(totalSeats);
        result.setTotalRevenue(totalRevenue);
        result.setAssociatedArtist(event.getAssociatedArtist());
        
        return result;
    }
}
