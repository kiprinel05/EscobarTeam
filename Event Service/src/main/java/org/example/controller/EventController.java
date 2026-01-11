package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.*;
import org.example.service.IEventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
@CrossOrigin(origins = "*")
public class EventController {

    private final IEventService eventService;

    @Autowired
    public EventController(IEventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponseDTO>> getAllEvents() {
        List<EventResponseDTO> events = eventService.getAllEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventResponseDTO> getEventById(@PathVariable Long id) {
        EventResponseDTO event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    @PostMapping
    public ResponseEntity<EventResponseDTO> createEvent(@Valid @RequestBody EventCreateDTO eventCreateDTO) {
        EventResponseDTO createdEvent = eventService.createEvent(eventCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdEvent);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventResponseDTO> updateEvent(@PathVariable Long id, 
                                                       @Valid @RequestBody EventDTO eventDTO) {
        EventResponseDTO updatedEvent = eventService.updateEvent(id, eventDTO);
        return ResponseEntity.ok(updatedEvent);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<EventResponseDTO>> searchByName(@RequestParam String name) {
        List<EventResponseDTO> events = eventService.searchByName(name);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/filter/stage")
    public ResponseEntity<List<EventResponseDTO>> filterByStage(@RequestParam Long stageId) {
        List<EventResponseDTO> events = eventService.filterByStage(stageId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/filter/date")
    public ResponseEntity<List<EventResponseDTO>> filterByDate(@RequestParam LocalDateTime startDate,
                                                              @RequestParam LocalDateTime endDate) {
        List<EventResponseDTO> events = eventService.filterByDate(startDate, endDate);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/filter/date/specific")
    public ResponseEntity<List<EventResponseDTO>> filterBySpecificDate(@RequestParam LocalDate date) {
        List<EventResponseDTO> events = eventService.filterBySpecificDate(date);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/filter/capacity")
    public ResponseEntity<List<EventResponseDTO>> filterByCapacity(@RequestParam Integer minCapacity) {
        List<EventResponseDTO> events = eventService.filterByCapacity(minCapacity);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/sort/date")
    public ResponseEntity<List<EventResponseDTO>> sortByDate(@RequestParam(defaultValue = "asc") String order) {
        List<EventResponseDTO> events = eventService.sortByDate(order);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/sort/capacity")
    public ResponseEntity<List<EventResponseDTO>> sortByCapacity(@RequestParam(defaultValue = "asc") String order) {
        List<EventResponseDTO> events = eventService.sortByCapacity(order);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/availability/stage")
    public ResponseEntity<List<StageAvailabilityDTO>> checkStageAvailability(@RequestParam LocalDateTime date) {
        List<StageAvailabilityDTO> availability = eventService.checkStageAvailability(date);
        return ResponseEntity.ok(availability);
    }

    @GetMapping("/schedule/stage/{stageId}")
    public ResponseEntity<List<EventResponseDTO>> getStageSchedule(@PathVariable Long stageId) {
        List<EventResponseDTO> schedule = eventService.getStageSchedule(stageId);
        return ResponseEntity.ok(schedule);
    }

    @GetMapping("/statistics")
    public ResponseEntity<EventStatisticsDTO> generateStatistics() {
        EventStatisticsDTO statistics = eventService.generateStatistics();
        return ResponseEntity.ok(statistics);
    }

    @GetMapping("/{id}/details")
    public ResponseEntity<EventWithArtistDetailsDTO> getEventWithArtistDetails(@PathVariable Long id) {
        EventWithArtistDetailsDTO result = eventService.getEventWithArtistDetails(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/book")
    public ResponseEntity<EventWithArtistDetailsDTO> bookEventSeats(@Valid @RequestBody EventBookingDTO bookingDTO) {
        EventWithArtistDetailsDTO result = eventService.bookEventSeats(bookingDTO);
        return ResponseEntity.ok(result);
    }
}
