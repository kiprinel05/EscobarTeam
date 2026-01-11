package org.example.ticketservice.service;

import org.example.ticketservice.dto.TicketCreateDTO;
import org.example.ticketservice.dto.TicketDTO;
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
    
    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
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
}
