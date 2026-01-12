package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketSummaryDTO {
    private String ticketType;
    private Integer quantity;
    private Double price;
}
