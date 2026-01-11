package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageInfoDTO {
    private Long stageId;
    private String stageName;
    private String location;
    private Integer capacity;
    private Integer numberOfEvents;
}

