package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.ArtistCreateDTO;
import org.example.dto.ArtistDTO;
import org.example.dto.ArtistStagesDTO;
import org.example.dto.ArtistWithEventsDTO;
import org.example.service.IArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/artists")
@CrossOrigin(origins = "*")
public class ArtistController {

    private final IArtistService artistService;

    @Autowired
    public ArtistController(IArtistService artistService) {
        this.artistService = artistService;
    }

    @GetMapping
    public ResponseEntity<List<ArtistDTO>> getAllArtists() {
        List<ArtistDTO> artists = artistService.getAllArtists();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ArtistDTO> getArtistById(@PathVariable Long id) {
        ArtistDTO artist = artistService.getArtistById(id);
        return ResponseEntity.ok(artist);
    }

    @PostMapping
    public ResponseEntity<ArtistDTO> createArtist(@Valid @RequestBody ArtistCreateDTO artistCreateDTO) {
        ArtistDTO createdArtist = artistService.createArtist(artistCreateDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdArtist);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ArtistDTO> updateArtist(@PathVariable Long id, @Valid @RequestBody ArtistDTO artistDTO) {
        ArtistDTO updatedArtist = artistService.updateArtist(id, artistDTO);
        return ResponseEntity.ok(updatedArtist);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable Long id) {
        artistService.deleteArtist(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ArtistDTO>> searchArtistsByName(@RequestParam String name) {
        List<ArtistDTO> artists = artistService.searchArtistsByName(name);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/filter/genre")
    public ResponseEntity<List<ArtistDTO>> filterArtistsByGenre(@RequestParam String genre) {
        List<ArtistDTO> artists = artistService.filterArtistsByGenre(genre);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/filter/nationality")
    public ResponseEntity<List<ArtistDTO>> filterArtistsByNationality(@RequestParam String nationality) {
        List<ArtistDTO> artists = artistService.filterArtistsByNationality(nationality);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/active")
    public ResponseEntity<List<ArtistDTO>> getActiveArtists() {
        List<ArtistDTO> artists = artistService.getActiveArtists();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/filter/rating")
    public ResponseEntity<List<ArtistDTO>> filterArtistsByMinRating(@RequestParam Double minRating) {
        List<ArtistDTO> artists = artistService.filterArtistsByMinRating(minRating);
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/sort/name")
    public ResponseEntity<List<ArtistDTO>> sortArtistsByName() {
        List<ArtistDTO> artists = artistService.sortArtistsByName();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/sort/rating")
    public ResponseEntity<List<ArtistDTO>> sortArtistsByRating() {
        List<ArtistDTO> artists = artistService.sortArtistsByRating();
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}/events")
    public ResponseEntity<ArtistWithEventsDTO> getArtistWithEvents(@PathVariable Long id) {
        ArtistWithEventsDTO result = artistService.getArtistWithEvents(id);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{id}/assign-event")
    public ResponseEntity<ArtistWithEventsDTO> assignArtistToEvent(
            @PathVariable Long id,
            @RequestParam String eventName) {
        ArtistWithEventsDTO result = artistService.assignArtistToEvent(id, eventName);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}/stages")
    public ResponseEntity<ArtistStagesDTO> getArtistStages(@PathVariable Long id) {
        ArtistStagesDTO result = artistService.getArtistStages(id);
        return ResponseEntity.ok(result);
    }
}

