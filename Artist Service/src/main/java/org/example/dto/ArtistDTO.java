package org.example.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO pentru transferul datelor Artist
 * Separă layer-ul de prezentare de cel de business
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDTO {
    private Long id;

    @NotBlank(message = "Numele artistului nu poate fi gol")
    @Size(min = 2, max = 100, message = "Numele trebuie să aibă între 2 și 100 de caractere")
    private String name;

    @NotBlank(message = "Genul muzical nu poate fi gol")
    @Size(max = 50, message = "Genul muzical nu poate depăși 50 de caractere")
    private String genre;

    @Min(value = 18, message = "Vârsta minimă este 18 ani")
    @Max(value = 100, message = "Vârsta maximă este 100 ani")
    private Integer age;

    @NotBlank(message = "Naționalitatea nu poate fi goală")
    @Size(max = 50, message = "Naționalitatea nu poate depăși 50 de caractere")
    private String nationality;

    @Email(message = "Email-ul trebuie să fie valid")
    private String email;

    private String biography;

    @DecimalMin(value = "0.0", message = "Rating-ul trebuie să fie pozitiv")
    @DecimalMax(value = "10.0", message = "Rating-ul nu poate depăși 10")
    private Double rating;

    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

