package org.example.ticketservice.service;

import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.mapper.TicketMapper;
import org.example.ticketservice.model.Ticket;
import org.example.ticketservice.repository.TicketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@Service
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final TicketMapper ticketMapper;

    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository, TicketMapper ticketMapper) {
        this.ticketRepository = ticketRepository;
        this.ticketMapper = ticketMapper;
    }

    @Override
    public TicketDTO createTicket(TicketDTO ticketDTO) {
        Ticket ticket = ticketMapper.toEntity(ticketDTO);
        Ticket savedTicket = ticketRepository.save(ticket);
        return ticketMapper.toDTO(savedTicket);
    }

    @Override
    public List<TicketDTO> getAllTickets() {
        return ticketRepository.findAll()
                .stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return ticketMapper.toDTO(ticket);
    }

    @Override
    public TicketDTO updateTicket(Long id, TicketDTO ticketDTO) {
        Ticket existing = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        existing.setBuyerName(ticketDTO.getBuyerName());
        existing.setEventName(ticketDTO.getEventName());
        existing.setPrice(ticketDTO.getPrice());
        existing.setStatus(ticketDTO.getStatus());
        Ticket updated = ticketRepository.save(existing);
        return ticketMapper.toDTO(updated);
    }

    @Override
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    @Override
    public List<TicketDTO> getTicketsByFestival(String eventName) {
        return ticketRepository.findByEventName(eventName)
                .stream()
                .map(ticketMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public int getAvailableSeats(String eventName) {

        int soldTickets = ticketRepository.countByEventName(eventName);

        return 1000 - soldTickets;
    }

    @Override
    public Map<String, Double> getRevenueByFestival() {
        List<Ticket> allTickets = ticketRepository.findAll();
        return allTickets.stream()
                .collect(Collectors.groupingBy(
                        Ticket::getEventName,
                        Collectors.summingDouble(Ticket::getPrice)
                ));
    }
}
