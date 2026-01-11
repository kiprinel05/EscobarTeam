package org.example.ticketservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TicketStatisticsDTO {
    private String eventName;
    private String stageName;
    private String ticketType;
    private Integer numberOfTickets;
    private Integer totalSeats;
    private Double totalRevenue;
    private String associatedArtist;
}

