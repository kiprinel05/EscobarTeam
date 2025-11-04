package org.example.ticketservice.mapper;

import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {

    public TicketDTO toDTO(Ticket ticket) {
        TicketDTO dto = new TicketDTO();
        dto.setId(ticket.getId());
        dto.setEventName(ticket.getEventName());
        dto.setBuyerName(ticket.getBuyerName());
        dto.setPrice(ticket.getPrice());
        dto.setStatus(ticket.getStatus());
        dto.setPurchaseDate(ticket.getPurchaseDate());
        return dto;
    }

    public Ticket toEntity(TicketDTO dto) {
        Ticket ticket = new Ticket();
        ticket.setId(dto.getId());
        ticket.setEventName(dto.getEventName());
        ticket.setBuyerName(dto.getBuyerName());
        ticket.setPrice(dto.getPrice());
        ticket.setStatus(dto.getStatus());
        ticket.setPurchaseDate(dto.getPurchaseDate());
        return ticket;
    }
}
