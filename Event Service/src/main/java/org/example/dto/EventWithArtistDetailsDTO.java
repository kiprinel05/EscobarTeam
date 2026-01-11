package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventWithArtistDetailsDTO {
    private Long id;
    private String name;
    private LocalDateTime date;
    private String stageName;
    private Integer capacity;
    private String associatedArtist;
    private String artistGenre;
    private Double artistRating;
    private Integer availableSeats;
    private Double revenue;
}

