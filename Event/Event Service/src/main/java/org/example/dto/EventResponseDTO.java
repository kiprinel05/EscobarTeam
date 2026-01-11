package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponseDTO {
    private Long id;
    private String name;
    private LocalDateTime date;
    private Long stageId;
    private String stageName;
    private String associatedArtist;
    private Integer capacity;
    private LocalDateTime createdAt;
}

