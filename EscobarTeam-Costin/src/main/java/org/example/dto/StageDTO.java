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

    @NotBlank(message = "Locația este obligatorie")
    private String location;

    @NotNull(message = "Capacitatea maximă este obligatorie")
    @Positive(message = "Capacitatea maximă trebuie să fie pozitivă")
    private Integer maxCapacity;
}

