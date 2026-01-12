package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistWithEventsDTO {
    private Long id;
    private String name;
    private String genre;
    private Integer age;
    private String nationality;
    private String email;
    private String biography;
    private Double rating;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<EventDTO> upcomingEvents;
    private Integer totalEvents;
    private String message;
}
