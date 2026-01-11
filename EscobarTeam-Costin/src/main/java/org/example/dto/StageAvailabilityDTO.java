package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageAvailabilityDTO {
    private Long stageId;
    private String stageName;
    private boolean available;
    private List<LocalDateTime> availableTimeSlots;
}

