package org.example.ticketservice.service;

import org.example.ticketservice.dto.TicketCreateDTO;
import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.dto.TicketStatisticsDTO;
import org.example.ticketservice.dto.TicketValidationDTO;
import org.example.ticketservice.dto.TicketWithEventDetailsDTO;

import java.util.List;
import java.util.Map;

public interface ITicketService {
    List<TicketDTO> getAllTickets();
    TicketDTO getTicketById(Long id);
    TicketDTO createTicket(TicketCreateDTO ticketCreateDTO);
    TicketDTO updateTicket(Long id, TicketDTO ticketDTO);
    void deleteTicket(Long id);
    
    List<TicketDTO> getTicketsByFestival(String eventName);
    int getAvailableSeats(String eventName);
    Map<String, Double> getRevenueByFestival();
    List<TicketDTO> getTicketsByType(String ticketType);
    Double getTotalRevenue();
    
    TicketWithEventDetailsDTO getTicketWithEventDetails(Long ticketId);
    TicketWithEventDetailsDTO validateAndCreateTicket(TicketValidationDTO validationDTO);
    
    TicketStatisticsDTO getTicketStatisticsByEventAndStage(String eventName, String stageName, String ticketType);
}
