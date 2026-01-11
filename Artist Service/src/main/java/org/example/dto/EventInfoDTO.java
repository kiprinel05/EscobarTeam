package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventInfoDTO {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String stageName;
    private Integer capacity;
}

