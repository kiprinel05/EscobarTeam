package org.example.ticketservice.service;

import org.example.ticketservice.dto.TicketDTO;
import java.util.List;
import java.util.Map;

public interface TicketService {
    TicketDTO createTicket(TicketDTO ticketDTO);
    List<TicketDTO> getAllTickets();
    TicketDTO getTicketById(Long id);
    TicketDTO updateTicket(Long id, TicketDTO ticketDTO);
    void deleteTicket(Long id);
    List<TicketDTO> getTicketsByFestival(String eventName);
    int getAvailableSeats(String eventName);
    Map<String, Double> getRevenueByFestival();
}
