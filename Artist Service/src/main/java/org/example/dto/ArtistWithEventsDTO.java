package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistWithEventsDTO {
    private ArtistDTO artist;
    private List<EventInfoDTO> events;
    private Integer totalEvents;
}

