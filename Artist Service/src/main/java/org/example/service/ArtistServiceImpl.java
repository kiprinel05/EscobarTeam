package org.example.service;

import org.example.client.EventServiceClient;
import org.example.dto.ArtistCreateDTO;
import org.example.dto.ArtistDTO;
import org.example.dto.ArtistWithEventsDTO;
import org.example.dto.EventDTO;
import org.example.entity.Artist;
import org.example.exception.ArtistNotFoundException;
import org.example.mapper.ArtistMapper;
import org.example.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing artists in the festival management system.
 * Provides business logic for CRUD operations, searching, filtering, and event scheduling.
 *
 * @author EscobarTeam
 */
@Service
@Transactional
public class ArtistServiceImpl implements IArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final EventServiceClient eventServiceClient;

    /**
     * Constructs a new {@code ArtistServiceImpl} with the required dependencies.
     *
     * @param artistRepository the repository for artist data access
     * @param artistMapper the mapper for converting between entities and DTOs
     * @param eventServiceClient the Feign client for communicating with the Event Service
     */
    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository, ArtistMapper artistMapper, EventServiceClient eventServiceClient) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
        this.eventServiceClient = eventServiceClient;
    }

    /**
     * Retrieves all artists from the database.
     *
     * @return a list of all {@code ArtistDTO} objects representing all artists
     */
    @Override
    public List<ArtistDTO> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an artist by its unique identifier.
     *
     * @param id the unique identifier of the artist to retrieve
     * @return the {@code ArtistDTO} object representing the artist with the specified ID
     * @throws ArtistNotFoundException if no artist exists with the given ID
     */
    @Override
    public ArtistDTO getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
        return artistMapper.toDTO(artist);
    }

    /**
     * Creates a new artist in the system.
     *
     * @param artistCreateDTO the DTO containing the data for the new artist
     * @return the {@code ArtistDTO} object representing the newly created artist
     */
    @Override
    public ArtistDTO createArtist(ArtistCreateDTO artistCreateDTO) {
        Artist artist = artistMapper.toEntity(artistCreateDTO);
        Artist savedArtist = artistRepository.save(artist);
        return artistMapper.toDTO(savedArtist);
    }

    /**
     * Updates an existing artist with new information.
     *
     * @param id the unique identifier of the artist to update
     * @param artistDTO the DTO containing the updated artist information
     * @return the {@code ArtistDTO} object representing the updated artist
     * @throws ArtistNotFoundException if no artist exists with the given ID
     */
    @Override
    public ArtistDTO updateArtist(Long id, ArtistDTO artistDTO) {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
        
        artistMapper.updateEntityFromDTO(artistDTO, existingArtist);
        Artist updatedArtist = artistRepository.save(existingArtist);
        return artistMapper.toDTO(updatedArtist);
    }

    /**
     * Deletes an artist from the system by its unique identifier.
     *
     * @param id the unique identifier of the artist to delete
     * @throws ArtistNotFoundException if no artist exists with the given ID
     */
    @Override
    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new ArtistNotFoundException(id);
        }
        artistRepository.deleteById(id);
    }

    /**
     * Searches for artists whose names contain the specified search string.
     * The search is case-insensitive.
     *
     * @param name the search string to match against artist names
     * @return a list of {@code ArtistDTO} objects matching the search criteria
     */
    @Override
    public List<ArtistDTO> searchArtistsByName(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name).stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters artists by their musical genre.
     *
     * @param genre the genre to filter by
     * @return a list of {@code ArtistDTO} objects representing artists with the specified genre
     */
    @Override
    public List<ArtistDTO> filterArtistsByGenre(String genre) {
        return artistRepository.findByGenre(genre).stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters artists by their nationality.
     *
     * @param nationality the nationality to filter by
     * @return a list of {@code ArtistDTO} objects representing artists with the specified nationality
     */
    @Override
    public List<ArtistDTO> filterArtistsByNationality(String nationality) {
        return artistRepository.findByNationality(nationality).stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all active artists from the database.
     *
     * @return a list of {@code ArtistDTO} objects representing all active artists
     */
    @Override
    public List<ArtistDTO> getActiveArtists() {
        return artistRepository.findByIsActiveTrue().stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters artists by minimum rating threshold.
     *
     * @param minRating the minimum rating value to filter by
     * @return a list of {@code ArtistDTO} objects representing artists with rating greater than or equal to the specified value
     */
    @Override
    public List<ArtistDTO> filterArtistsByMinRating(Double minRating) {
        return artistRepository.findByRatingGreaterThanEqual(minRating).stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all artists sorted alphabetically by name in ascending order.
     *
     * @return a list of {@code ArtistDTO} objects sorted by name
     */
    @Override
    public List<ArtistDTO> sortArtistsByName() {
        return artistRepository.findAll().stream()
                .sorted(Comparator.comparing(Artist::getName))
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all artists sorted by rating in descending order.
     * Artists without a rating are excluded from the results.
     *
     * @return a list of {@code ArtistDTO} objects sorted by rating from highest to lowest
     */
    @Override
    public List<ArtistDTO> sortArtistsByRating() {
        return artistRepository.findAll().stream()
                .filter(artist -> artist.getRating() != null)
                .sorted(Comparator.comparing(Artist::getRating).reversed())
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves an artist along with their associated upcoming events.
     * The events are filtered to show only future events and are sorted by date.
     *
     * @param id the unique identifier of the artist
     * @param region the region code for localization (e.g., "EU-RO", "US")
     * @param language the language code for message localization (e.g., "ro-RO", "en-US")
     * @return a {@code ArtistWithEventsDTO} object containing artist information and upcoming events
     * @throws ArtistNotFoundException if no artist exists with the given ID
     */
    @Override
    public ArtistWithEventsDTO getArtistWithEvents(Long id, String region, String language) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
        
        ArtistDTO artistDTO = artistMapper.toDTO(artist);
        
        List<EventDTO> events = eventServiceClient.filterEventsByArtist(
                artist.getName(),
                "Gateway-Service",
                region,
                language
        );
        
        List<EventDTO> upcomingEvents = events.stream()
                .filter(event -> event.getDate() != null && event.getDate().isAfter(LocalDateTime.now()))
                .sorted(Comparator.comparing(EventDTO::getDate))
                .collect(Collectors.toList());
        
        String message = getLocalizedMessage(language, artist.getName(), upcomingEvents.size());
        
        return ArtistWithEventsDTO.builder()
                .id(artistDTO.getId())
                .name(artistDTO.getName())
                .genre(artistDTO.getGenre())
                .age(artistDTO.getAge())
                .nationality(artistDTO.getNationality())
                .email(artistDTO.getEmail())
                .biography(artistDTO.getBiography())
                .rating(artistDTO.getRating())
                .isActive(artistDTO.getIsActive())
                .createdAt(artistDTO.getCreatedAt())
                .updatedAt(artistDTO.getUpdatedAt())
                .upcomingEvents(upcomingEvents)
                .totalEvents(events.size())
                .message(message)
                .build();
    }

    /**
     * Schedules an event for a specific artist.
     * Validates that the event exists and is associated with the artist before scheduling.
     *
     * @param id the unique identifier of the artist
     * @param eventName the name of the event to schedule
     * @param region the region code for localization (e.g., "EU-RO", "US")
     * @param language the language code for message localization (e.g., "ro-RO", "en-US")
     * @return a {@code ArtistWithEventsDTO} object containing updated artist information with events
     * @throws ArtistNotFoundException if no artist exists with the given ID
     * @throws RuntimeException if the event does not exist or is not associated with the artist
     */
    @Override
    public ArtistWithEventsDTO scheduleEventForArtist(Long id, String eventName, String region, String language) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
        
        List<EventDTO> events = eventServiceClient.searchEventsByArtist(
                eventName,
                "Gateway-Service",
                region,
                language
        );
        
        boolean eventExists = events.stream()
                .anyMatch(event -> event.getName().equalsIgnoreCase(eventName) &&
                        event.getAssociatedArtist().contains(artist.getName()));
        
        if (!eventExists) {
            throw new RuntimeException("Evenimentul '" + eventName + "' nu exista sau nu este asociat cu artistul " + artist.getName());
        }
        
        return getArtistWithEvents(id, region, language);
    }

    /**
     * Generates a localized message about the number of upcoming events for an artist.
     *
     * @param language the language code for message localization (e.g., "ro-RO", "en-US")
     * @param artistName the name of the artist
     * @param eventCount the number of upcoming events
     * @return a localized message string
     */
    private String getLocalizedMessage(String language, String artistName, int eventCount) {
        if ("en-US".equalsIgnoreCase(language) || "en".equalsIgnoreCase(language)) {
            return String.format("%s has %d upcoming event(s).", artistName, eventCount);
        } else {
            return String.format("%s are %d eveniment(e) programat(e).", artistName, eventCount);
        }
    }
}

