package org.example.ticketservice.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tickets")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String eventName;
    private String buyerName;
    private double price;

    @Enumerated(EnumType.STRING)
    private Status status; // ACTIVE, CANCELLED, etc.

    private LocalDateTime purchaseDate;
}
