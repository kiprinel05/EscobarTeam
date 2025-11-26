package org.example.ticketservice.controller;

import org.example.ticketservice.dto.TicketDTO;
import org.example.ticketservice.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    private final TicketService ticketService;

    @Autowired
    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // GET - toate biletele
    @GetMapping
    public List<TicketDTO> getAllTickets() {
        return ticketService.getAllTickets();
    }

    // GET - un bilet dupa ID
    @GetMapping("/{id}")
    public TicketDTO getTicketById(@PathVariable Long id) {
        return ticketService.getTicketById(id);
    }

    // POST - creare bilet nou
    @PostMapping
    public TicketDTO createTicket(@RequestBody TicketDTO ticketDTO) {
        return ticketService.createTicket(ticketDTO);
    }

    // PUT - actualizare bilet
    @PutMapping("/{id}")
    public TicketDTO updateTicket(@PathVariable Long id, @RequestBody TicketDTO ticketDTO) {
        return ticketService.updateTicket(id, ticketDTO);
    }

    // DELETE - stergere bilet
    @DeleteMapping("/{id}")
    public void deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
    }

    // GET - filtrare bilete dupa festival
    @GetMapping("/festival/{eventName}")
    public List<TicketDTO> getTicketsByFestival(@PathVariable String eventName) {
        return ticketService.getTicketsByFestival(eventName);
    }

    // GET - verificare locuri disponibile pentru un festival
    @GetMapping("/festival/{eventName}/available-seats")
    public int getAvailableSeats(@PathVariable String eventName) {
        return ticketService.getAvailableSeats(eventName);
    }

    // GET - venit total pe fiecare festival
    @GetMapping("/revenue/by-festival")
    public Map<String, Double> getRevenueByFestival() {
        return ticketService.getRevenueByFestival();
    }
}
