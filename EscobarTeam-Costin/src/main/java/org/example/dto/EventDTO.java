package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventDTO {
    private Long id;

    @NotBlank(message = "Numele concertului este obligatoriu")
    private String name;

    @NotNull(message = "Data este obligatorie")
    private LocalDateTime date;

    @NotNull(message = "ID-ul scenei este obligatoriu")
    private Long stageId;

    @NotBlank(message = "Artistul asociat este obligatoriu")
    private String associatedArtist;

    @NotNull(message = "Capacitatea este obligatorie")
    @Positive(message = "Capacitatea trebuie să fie pozitivă")
    private Integer capacity;

    private LocalDateTime createdAt;
}

