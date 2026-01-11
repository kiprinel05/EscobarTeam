package org.example.ticketservice.mapper;

import org.example.ticketservice.dto.TicketCreateDTO;
import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.model.Ticket;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper {
    
    public TicketDTO toDTO(Ticket ticket) {
        if (ticket == null) {
            return null;
        }
        return TicketDTO.builder()
                .id(ticket.getId())
                .eventName(ticket.getEventName())
                .ticketType(ticket.getTicketType())
                .price(ticket.getPrice())
                .quantity(ticket.getQuantity())
                .buyerName(ticket.getBuyerName())
                .buyerEmail(ticket.getBuyerEmail())
                .purchaseDate(ticket.getPurchaseDate())
                .isActive(ticket.getIsActive())
                .createdAt(ticket.getCreatedAt())
                .build();
    }
    
    public Ticket toEntity(TicketCreateDTO ticketCreateDTO) {
        if (ticketCreateDTO == null) {
            return null;
        }
        return Ticket.builder()
                .eventName(ticketCreateDTO.getEventName())
                .ticketType(ticketCreateDTO.getTicketType())
                .price(ticketCreateDTO.getPrice())
                .quantity(ticketCreateDTO.getQuantity())
                .buyerName(ticketCreateDTO.getBuyerName())
                .buyerEmail(ticketCreateDTO.getBuyerEmail())
                .isActive(true)
                .build();
    }
    
    public Ticket toEntity(TicketDTO ticketDTO) {
        if (ticketDTO == null) {
            return null;
        }
        return Ticket.builder()
                .id(ticketDTO.getId())
                .eventName(ticketDTO.getEventName())
                .ticketType(ticketDTO.getTicketType())
                .price(ticketDTO.getPrice())
                .quantity(ticketDTO.getQuantity())
                .buyerName(ticketDTO.getBuyerName())
                .buyerEmail(ticketDTO.getBuyerEmail())
                .purchaseDate(ticketDTO.getPurchaseDate())
                .isActive(ticketDTO.getIsActive())
                .build();
    }
    
    public void updateEntityFromDTO(TicketDTO ticketDTO, Ticket ticket) {
        if (ticketDTO == null || ticket == null) {
            return;
        }
        if (ticketDTO.getEventName() != null) {
            ticket.setEventName(ticketDTO.getEventName());
        }
        if (ticketDTO.getTicketType() != null) {
            ticket.setTicketType(ticketDTO.getTicketType());
        }
        if (ticketDTO.getPrice() != null) {
            ticket.setPrice(ticketDTO.getPrice());
        }
        if (ticketDTO.getQuantity() != null) {
            ticket.setQuantity(ticketDTO.getQuantity());
        }
        if (ticketDTO.getBuyerName() != null) {
            ticket.setBuyerName(ticketDTO.getBuyerName());
        }
        if (ticketDTO.getBuyerEmail() != null) {
            ticket.setBuyerEmail(ticketDTO.getBuyerEmail());
        }
        if (ticketDTO.getIsActive() != null) {
            ticket.setIsActive(ticketDTO.getIsActive());
        }
    }
}
