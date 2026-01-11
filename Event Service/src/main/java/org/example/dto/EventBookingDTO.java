package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventBookingDTO {
    @NotBlank(message = "Numele evenimentului este obligatoriu")
    private String eventName;
    
    @NotNull(message = "Numărul de locuri este obligatoriu")
    @Positive(message = "Numărul de locuri trebuie să fie pozitiv")
    private Integer numberOfSeats;
    
    private String artistName;
}

