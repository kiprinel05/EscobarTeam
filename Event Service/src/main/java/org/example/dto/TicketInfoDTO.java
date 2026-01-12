package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketInfoDTO {
    private String eventName;
    private Integer availableSeats;
    private Double totalRevenue;
    private List<TicketSummaryDTO> tickets;
}
