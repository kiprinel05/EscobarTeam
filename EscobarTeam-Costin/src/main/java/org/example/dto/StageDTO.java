package org.example.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StageDTO {
    private Long id;

    @NotBlank(message = "Numele scenei este obligatoriu")
    private String name;

    @NotBlank(message = "Locatia este obligatorie")
    private String location;

    @NotNull(message = "Capacitatea maxima este obligatorie")
    @Positive(message = "Capacitatea maxima trebuie sa fie pozitiva")
    private Integer maxCapacity;
}

