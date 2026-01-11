package org.example.ticketservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Event name cannot be empty")
    @Column(name = "event_name", nullable = false)
    private String eventName;
    
    @NotBlank(message = "Ticket type cannot be empty")
    @Column(name = "ticket_type", nullable = false)
    private String ticketType; // VIP, GENERAL, EARLY_BIRD
    
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    @Column(name = "price", nullable = false)
    private Double price;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    
    @Column(name = "buyer_name")
    private String buyerName;
    
    @Email(message = "Email must be valid")
    @Column(name = "buyer_email")
    private String buyerEmail;
    
    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;
    
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (purchaseDate == null) {
            purchaseDate = LocalDateTime.now();
        }
    }
}
