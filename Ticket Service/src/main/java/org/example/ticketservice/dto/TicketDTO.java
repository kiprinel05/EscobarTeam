package org.example.ticketservice.dto;

import org.example.ticketservice.model.Status;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDTO {
    private Long id;
    private String eventName;
    private String buyerName;
    private double price;
    private Status status;
    private LocalDateTime purchaseDate;
}
