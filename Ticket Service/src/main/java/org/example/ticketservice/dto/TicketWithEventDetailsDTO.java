package org.example.ticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketWithEventDetailsDTO {
    private Long id;
    private String eventName;
    private String ticketType;
    private Double price;
    private Integer quantity;
    private String buyerName;
    private String buyerEmail;
    private LocalDateTime purchaseDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    
    private LocalDateTime eventDate;
    private String stageName;
    private String associatedArtist;
    private Integer eventCapacity;
    private String validationMessage; 
}
