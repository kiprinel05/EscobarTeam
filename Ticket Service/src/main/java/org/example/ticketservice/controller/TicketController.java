package org.example.ticketservice.controller;

import jakarta.validation.Valid;
import org.example.ticketservice.dto.TicketCreateDTO;
import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.dto.TicketStatisticsDTO;
import org.example.ticketservice.dto.TicketValidationDTO;
import org.example.ticketservice.dto.TicketWithEventDetailsDTO;
import org.example.ticketservice.service.ITicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@CrossOrigin(origins = "*")
public class TicketController {

    private final ITicketService ticketService;

    @Autowired
    public TicketController(ITicketService ticketService) {
        this.ticketService = ticketService;
    }

    // GET - toate biletele
    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        List<TicketDTO> tickets = ticketService.getAllTickets();
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/statistics")
    public ResponseEntity<TicketStatisticsDTO> getTicketStatistics(
            @RequestParam String eventName,
            @RequestParam String stageName,
            @RequestParam(defaultValue = "VIP") String ticketType) {
        TicketStatisticsDTO result = ticketService.getTicketStatisticsByEventAndStage(eventName, stageName, ticketType);
        return ResponseEntity.ok(result);
    }

    // GET - un bilet dupa ID
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        TicketDTO ticket = ticketService.getTicketById(id);
        return ResponseEntity.ok(ticket);
    }

    // POST - creare bilet nou
    @PostMapping
    public ResponseEntity<TicketDTO> createTicket(@Valid @RequestBody TicketCreateDTO ticketCreateDTO) {
        TicketDTO createdTicket = ticketService.createTicket(ticketCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTicket);
    }

    // PUT - actualizare bilet
    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(@PathVariable Long id, @Valid @RequestBody TicketDTO ticketDTO) {
        TicketDTO updatedTicket = ticketService.updateTicket(id, ticketDTO);
        return ResponseEntity.ok(updatedTicket);
    }

    // DELETE - stergere bilet
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.noContent().build();
    }

    // GET - filtrare bilete dupa festival
    @GetMapping("/festival/{eventName}")
    public ResponseEntity<List<TicketDTO>> getTicketsByFestival(@PathVariable String eventName) {
        List<TicketDTO> tickets = ticketService.getTicketsByFestival(eventName);
        return ResponseEntity.ok(tickets);
    }

    // GET - verificare locuri disponibile pentru un festival
    @GetMapping("/festival/{eventName}/available-seats")
    public ResponseEntity<Integer> getAvailableSeats(@PathVariable String eventName) {
        int availableSeats = ticketService.getAvailableSeats(eventName);
        return ResponseEntity.ok(availableSeats);
    }

    // GET - venit total pe fiecare festival
    @GetMapping("/revenue/by-festival")
    public ResponseEntity<Map<String, Double>> getRevenueByFestival() {
        Map<String, Double> revenue = ticketService.getRevenueByFestival();
        return ResponseEntity.ok(revenue);
    }
    
    // GET - filtrare bilete dupa tip
    @GetMapping("/type/{ticketType}")
    public ResponseEntity<List<TicketDTO>> getTicketsByType(@PathVariable String ticketType) {
        List<TicketDTO> tickets = ticketService.getTicketsByType(ticketType);
        return ResponseEntity.ok(tickets);
    }
    
    // GET - venit total
    @GetMapping("/revenue/total")
    public ResponseEntity<Double> getTotalRevenue() {
        Double totalRevenue = ticketService.getTotalRevenue();
        return ResponseEntity.ok(totalRevenue);
    }

    // GET - bilet cu detalii despre eveniment
    @GetMapping("/{id}/event-details")
    public ResponseEntity<TicketWithEventDetailsDTO> getTicketWithEventDetails(@PathVariable Long id) {
        TicketWithEventDetailsDTO result = ticketService.getTicketWithEventDetails(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/validate-and-create")
    public ResponseEntity<TicketWithEventDetailsDTO> validateAndCreateTicket(
            @Valid @RequestBody TicketValidationDTO validationDTO) {
        TicketWithEventDetailsDTO result = ticketService.validateAndCreateTicket(validationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
