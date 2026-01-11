package org.example.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "artists")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
@ToString
public class Artist extends BaseEntity {

    @NotBlank(message = "Numele artistului nu poate fi gol")
    @Size(min = 2, max = 100, message = "Numele trebuie să aiba intre 2 si 100 de caract.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Genul muzical nu poate fi gol")
    @Size(max = 50, message = "Genul muzical nu poate depasi 50 de caract.")
    @Column(name = "genre", nullable = false, length = 50)
    private String genre;

    @Min(value = 18, message = "Varsta minima este 18 ani")
    @Max(value = 100, message = "Varsta maxima este 100 ani")
    @Column(name = "age")
    private Integer age;

    @NotBlank(message = "Nationalitatea nu poate fi goala")
    @Size(max = 50, message = "Nationalitatea nu poate depasi 50 de caract.")
    @Column(name = "nationality", nullable = false, length = 50)
    private String nationality;

    @Email(message = "Email ul trebuie sa fie valid")
    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "biography", columnDefinition = "TEXT")
    private String biography;

    @DecimalMin(value = "0.0", message = "Rating ul trebuie sa fie pozitiv")
    @DecimalMax(value = "10.0", message = "Rating ul nu poate depasi 10")
    @Column(name = "rating", columnDefinition = "NUMERIC(3,2)")
    private Double rating;

    @Column(name = "is_active")
    private Boolean isActive = true;

    public Artist(String name, String genre, String nationality) {
        this.name = name;
        this.genre = genre;
        this.nationality = nationality;
        this.isActive = true;
    }
}

