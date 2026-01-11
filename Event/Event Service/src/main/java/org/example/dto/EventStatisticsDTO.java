package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventStatisticsDTO {
    private Long totalEvents;
    private Long totalParticipants;
    private Map<LocalDate, Long> eventsPerDay;
    private Map<LocalDate, Long> participantsPerDay;
}

