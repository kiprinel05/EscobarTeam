package org.example.ticketservice.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketCreateDTO {
    
    @NotBlank(message = "Event name cannot be empty")
    private String eventName;
    
    @NotBlank(message = "Ticket type cannot be empty")
    private String ticketType;
    
    @NotNull(message = "Price cannot be null")
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private Double price;
    
    @NotNull(message = "Quantity cannot be null")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private String buyerName;
    
    @Email(message = "Email must be valid")
    private String buyerEmail;
}
