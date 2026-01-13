package org.example.service;

import org.example.dto.ArtistCreateDTO;
import org.example.dto.ArtistDTO;
import org.example.dto.ArtistWithEventsDTO;

import java.util.List;

/**
 * Service interface for managing artists in the festival management system.
 * Provides operations for CRUD operations, searching, filtering, and scheduling events for artists.
 *
 * @author EscobarTeam
 */
public interface IArtistService {
    /**
     * Retrieves all artists from the database.
     *
     * @return a list of all {@code ArtistDTO} objects representing all artists
     */
    List<ArtistDTO> getAllArtists();
    
    /**
     * Retrieves an artist by its unique identifier.
     *
     * @param id the unique identifier of the artist to retrieve
     * @return the {@code ArtistDTO} object representing the artist with the specified ID
     * @throws org.example.exception.ArtistNotFoundException if no artist exists with the given ID
     */
    ArtistDTO getArtistById(Long id);
    
    /**
     * Creates a new artist in the system.
     *
     * @param artistCreateDTO the DTO containing the data for the new artist
     * @return the {@code ArtistDTO} object representing the newly created artist
     */
    ArtistDTO createArtist(ArtistCreateDTO artistCreateDTO);
    
    /**
     * Updates an existing artist with new information.
     *
     * @param id the unique identifier of the artist to update
     * @param artistDTO the DTO containing the updated artist information
     * @return the {@code ArtistDTO} object representing the updated artist
     * @throws org.example.exception.ArtistNotFoundException if no artist exists with the given ID
     */
    ArtistDTO updateArtist(Long id, ArtistDTO artistDTO);
    
    /**
     * Deletes an artist from the system by its unique identifier.
     *
     * @param id the unique identifier of the artist to delete
     * @throws org.example.exception.ArtistNotFoundException if no artist exists with the given ID
     */
    void deleteArtist(Long id);
    
    /**
     * Searches for artists whose names contain the specified search string.
     * The search is case-insensitive.
     *
     * @param name the search string to match against artist names
     * @return a list of {@code ArtistDTO} objects matching the search criteria
     */
    List<ArtistDTO> searchArtistsByName(String name);
    
    /**
     * Filters artists by their musical genre.
     *
     * @param genre the genre to filter by
     * @return a list of {@code ArtistDTO} objects representing artists with the specified genre
     */
    List<ArtistDTO> filterArtistsByGenre(String genre);
    
    /**
     * Filters artists by their nationality.
     *
     * @param nationality the nationality to filter by
     * @return a list of {@code ArtistDTO} objects representing artists with the specified nationality
     */
    List<ArtistDTO> filterArtistsByNationality(String nationality);
    
    /**
     * Retrieves all active artists from the database.
     *
     * @return a list of {@code ArtistDTO} objects representing all active artists
     */
    List<ArtistDTO> getActiveArtists();
    
    /**
     * Filters artists by minimum rating threshold.
     *
     * @param minRating the minimum rating value to filter by
     * @return a list of {@code ArtistDTO} objects representing artists with rating greater than or equal to the specified value
     */
    List<ArtistDTO> filterArtistsByMinRating(Double minRating);
    
    /**
     * Retrieves all artists sorted alphabetically by name in ascending order.
     *
     * @return a list of {@code ArtistDTO} objects sorted by name
     */
    List<ArtistDTO> sortArtistsByName();
    
    /**
     * Retrieves all artists sorted by rating in descending order.
     * Artists without a rating are excluded from the results.
     *
     * @return a list of {@code ArtistDTO} objects sorted by rating from highest to lowest
     */
    List<ArtistDTO> sortArtistsByRating();
    
    /**
     * Retrieves an artist along with their associated upcoming events.
     * The events are filtered to show only future events and are sorted by date.
     *
     * @param id the unique identifier of the artist
     * @param region the region code for localization (e.g., "EU-RO", "US")
     * @param language the language code for message localization (e.g., "ro-RO", "en-US")
     * @return a {@code ArtistWithEventsDTO} object containing artist information and upcoming events
     * @throws org.example.exception.ArtistNotFoundException if no artist exists with the given ID
     */
    ArtistWithEventsDTO getArtistWithEvents(Long id, String region, String language);
    
    /**
     * Schedules an event for a specific artist.
     * Validates that the event exists and is associated with the artist before scheduling.
     *
     * @param id the unique identifier of the artist
     * @param eventName the name of the event to schedule
     * @param region the region code for localization (e.g., "EU-RO", "US")
     * @param language the language code for message localization (e.g., "ro-RO", "en-US")
     * @return a {@code ArtistWithEventsDTO} object containing updated artist information with events
     * @throws org.example.exception.ArtistNotFoundException if no artist exists with the given ID
     * @throws RuntimeException if the event does not exist or is not associated with the artist
     */
    ArtistWithEventsDTO scheduleEventForArtist(Long id, String eventName, String region, String language);
}

