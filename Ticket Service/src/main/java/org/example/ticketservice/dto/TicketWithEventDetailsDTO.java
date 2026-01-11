package org.example.ticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketWithEventDetailsDTO {
    private Long ticketId;
    private String eventName;
    private LocalDateTime eventDate;
    private String stageName;
    private String associatedArtist;
    private Integer eventCapacity;
    private String ticketType;
    private Double price;
    private Integer quantity;
    private Double totalPrice;
}

