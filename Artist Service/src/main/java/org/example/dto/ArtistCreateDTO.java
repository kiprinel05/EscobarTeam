package org.example.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistCreateDTO {
    @NotBlank(message = "Numele artistului nu poate fi gol")
    @Size(min = 2, max = 100, message = "Numele trebuie sa aiba intre 2 si 100 de caract.")
    private String name;

    @NotBlank(message = "Genul muzical nu poate fi gol")
    @Size(max = 50, message = "Genul muzical nu poate depasi 50 de caract.")
    private String genre;

    @Min(value = 18, message = "Varsta minima este 18 ani")
    @Max(value = 100, message = "Varsta max este 100 ani")
    private Integer age;

    @NotBlank(message = "Nationalitatea nu poate fi goala")
    @Size(max = 50, message = "Nationalitatea nu poate depasi 50 de caractere")
    private String nationality;

    @Email(message = "Email ul trebuie sa fie valid")
    private String email;

    private String biography;

    @DecimalMin(value = "0.0", message = "Rating ul trebuie sa fie pozitiv")
    @DecimalMax(value = "10.0", message = "Rating ul nu poate depasi 10")
    private Double rating;
}

