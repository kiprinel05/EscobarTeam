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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistServiceImplTest {

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private ArtistMapper artistMapper;

    @Mock
    private EventServiceClient eventServiceClient;

    @InjectMocks
    private ArtistServiceImpl artistService;

    private Artist artist;
    private ArtistDTO artistDTO;
    private ArtistCreateDTO artistCreateDTO;

    @BeforeEach
    void setUp() {
        artist = new Artist();
        artist.setId(1L);
        artist.setName("Test Artist");
        artist.setGenre("Rock");
        artist.setAge(30);
        artist.setNationality("Romanian");
        artist.setEmail("test@example.com");
        artist.setBiography("Test biography");
        artist.setRating(8.5);
        artist.setIsActive(true);
        artist.setCreatedAt(LocalDateTime.now());
        artist.setUpdatedAt(LocalDateTime.now());

        artistDTO = ArtistDTO.builder()
                .id(1L)
                .name("Test Artist")
                .genre("Rock")
                .age(30)
                .nationality("Romanian")
                .email("test@example.com")
                .biography("Test biography")
                .rating(8.5)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        artistCreateDTO = ArtistCreateDTO.builder()
                .name("New Artist")
                .genre("Pop")
                .age(25)
                .nationality("American")
                .email("new@example.com")
                .biography("New biography")
                .rating(7.0)
                .build();
    }

    @Test
    void testGetAllArtists_Success() {
        // Given
        List<Artist> artists = Arrays.asList(artist);
        when(artistRepository.findAll()).thenReturn(artists);
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.getAllArtists();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Artist", result.get(0).getName());
        verify(artistRepository).findAll();
        verify(artistMapper).toDTO(artist);
    }

    @Test
    void testGetArtistById_Success() {
        // Given
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        // When
        ArtistDTO result = artistService.getArtistById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Artist", result.getName());
        verify(artistRepository).findById(1L);
        verify(artistMapper).toDTO(artist);
    }

    @Test
    void testGetArtistById_NotFound() {
        // Given
        when(artistRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ArtistNotFoundException.class, () -> artistService.getArtistById(999L));
        verify(artistRepository).findById(999L);
        verify(artistMapper, never()).toDTO(any());
    }

    @Test
    void testCreateArtist_Success() {
        // Given
        Artist newArtist = new Artist();
        newArtist.setId(2L);
        newArtist.setName("New Artist");
        when(artistMapper.toEntity(artistCreateDTO)).thenReturn(newArtist);
        when(artistRepository.save(any(Artist.class))).thenReturn(newArtist);
        ArtistDTO newArtistDTO = ArtistDTO.builder().id(2L).name("New Artist").build();
        when(artistMapper.toDTO(newArtist)).thenReturn(newArtistDTO);

        // When
        ArtistDTO result = artistService.createArtist(artistCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals(2L, result.getId());
        verify(artistMapper).toEntity(artistCreateDTO);
        verify(artistRepository).save(newArtist);
        verify(artistMapper).toDTO(newArtist);
    }

    @Test
    void testUpdateArtist_Success() {
        // Given
        ArtistDTO updatedDTO = ArtistDTO.builder()
                .id(1L)
                .name("Updated Artist")
                .genre("Jazz")
                .build();
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistRepository.save(artist)).thenReturn(artist);
        when(artistMapper.toDTO(artist)).thenReturn(updatedDTO);

        // When
        ArtistDTO result = artistService.updateArtist(1L, updatedDTO);

        // Then
        assertNotNull(result);
        assertEquals("Updated Artist", result.getName());
        verify(artistRepository).findById(1L);
        verify(artistMapper).updateEntityFromDTO(updatedDTO, artist);
        verify(artistRepository).save(artist);
    }

    @Test
    void testUpdateArtist_NotFound() {
        // Given
        when(artistRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ArtistNotFoundException.class, () -> 
                artistService.updateArtist(999L, artistDTO));
        verify(artistRepository).findById(999L);
        verify(artistRepository, never()).save(any());
    }

    @Test
    void testDeleteArtist_Success() {
        // Given
        when(artistRepository.existsById(1L)).thenReturn(true);

        // When
        artistService.deleteArtist(1L);

        // Then
        verify(artistRepository).existsById(1L);
        verify(artistRepository).deleteById(1L);
    }

    @Test
    void testDeleteArtist_NotFound() {
        // Given
        when(artistRepository.existsById(999L)).thenReturn(false);

        // When & Then
        assertThrows(ArtistNotFoundException.class, () -> artistService.deleteArtist(999L));
        verify(artistRepository).existsById(999L);
        verify(artistRepository, never()).deleteById(any());
    }

    @Test
    void testSearchArtistsByName_Success() {
        // Given
        List<Artist> artists = Arrays.asList(artist);
        when(artistRepository.findByNameContainingIgnoreCase("Test")).thenReturn(artists);
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.searchArtistsByName("Test");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository).findByNameContainingIgnoreCase("Test");
    }

    @Test
    void testFilterArtistsByGenre_Success() {
        // Given
        List<Artist> artists = Arrays.asList(artist);
        when(artistRepository.findByGenre("Rock")).thenReturn(artists);
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.filterArtistsByGenre("Rock");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository).findByGenre("Rock");
    }

    @Test
    void testFilterArtistsByNationality_Success() {
        // Given
        List<Artist> artists = Arrays.asList(artist);
        when(artistRepository.findByNationality("Romanian")).thenReturn(artists);
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.filterArtistsByNationality("Romanian");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository).findByNationality("Romanian");
    }

    @Test
    void testGetActiveArtists_Success() {
        // Given
        List<Artist> artists = Arrays.asList(artist);
        when(artistRepository.findByIsActiveTrue()).thenReturn(artists);
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.getActiveArtists();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository).findByIsActiveTrue();
    }

    @Test
    void testFilterArtistsByMinRating_Success() {
        // Given
        List<Artist> artists = Arrays.asList(artist);
        when(artistRepository.findByRatingGreaterThanEqual(8.0)).thenReturn(artists);
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.filterArtistsByMinRating(8.0);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(artistRepository).findByRatingGreaterThanEqual(8.0);
    }

    @Test
    void testSortArtistsByName_Success() {
        // Given
        Artist artist2 = new Artist();
        artist2.setName("Another Artist");
        List<Artist> artists = Arrays.asList(artist, artist2);
        when(artistRepository.findAll()).thenReturn(artists);
        when(artistMapper.toDTO(any(Artist.class))).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.sortArtistsByName();

        // Then
        assertNotNull(result);
        verify(artistRepository).findAll();
    }

    @Test
    void testSortArtistsByRating_Success() {
        // Given
        Artist artist2 = new Artist();
        artist2.setRating(9.0);
        List<Artist> artists = Arrays.asList(artist, artist2);
        when(artistRepository.findAll()).thenReturn(artists);
        when(artistMapper.toDTO(any(Artist.class))).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.sortArtistsByRating();

        // Then
        assertNotNull(result);
        verify(artistRepository).findAll();
    }

    @Test
    void testSortArtistsByRating_WithNullRating() {
        // Given
        Artist artistWithNullRating = new Artist();
        artistWithNullRating.setRating(null);
        List<Artist> artists = Arrays.asList(artist, artistWithNullRating);
        when(artistRepository.findAll()).thenReturn(artists);
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        // When
        List<ArtistDTO> result = artistService.sortArtistsByRating();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size()); // Only artist with rating should be included
    }

    @Test
    void testGetArtistWithEvents_Success() {
        // Given
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        EventDTO event1 = new EventDTO();
        event1.setId(1L);
        event1.setName("Concert 1");
        event1.setDate(LocalDateTime.now().plusDays(5));
        event1.setAssociatedArtist("Test Artist");

        EventDTO event2 = new EventDTO();
        event2.setId(2L);
        event2.setName("Concert 2");
        event2.setDate(LocalDateTime.now().minusDays(1)); // Past event
        event2.setAssociatedArtist("Test Artist");

        List<EventDTO> events = Arrays.asList(event1, event2);
        when(eventServiceClient.filterEventsByArtist(eq("Test Artist"), eq("Gateway-Service"), 
                anyString(), anyString())).thenReturn(events);

        // When
        ArtistWithEventsDTO result = artistService.getArtistWithEvents(1L, "EU-RO", "ro-RO");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(1, result.getUpcomingEvents().size()); // Only future events
        assertEquals(2, result.getTotalEvents());
        assertTrue(result.getMessage().contains("Test Artist"));
        verify(artistRepository).findById(1L);
        verify(eventServiceClient).filterEventsByArtist(eq("Test Artist"), eq("Gateway-Service"), 
                eq("EU-RO"), eq("ro-RO"));
    }

    @Test
    void testGetArtistWithEvents_EnglishLanguage() {
        // Given
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);
        when(eventServiceClient.filterEventsByArtist(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        // When
        ArtistWithEventsDTO result = artistService.getArtistWithEvents(1L, "US", "en-US");

        // Then
        assertNotNull(result);
        assertTrue(result.getMessage().contains("has") || result.getMessage().contains("event"));
    }

    @Test
    void testGetArtistWithEvents_NotFound() {
        // Given
        when(artistRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ArtistNotFoundException.class, () -> 
                artistService.getArtistWithEvents(999L, "EU-RO", "ro-RO"));
    }

    @Test
    void testScheduleEventForArtist_Success() {
        // Given
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(artistMapper.toDTO(artist)).thenReturn(artistDTO);

        EventDTO event = new EventDTO();
        event.setName("Test Event");
        event.setAssociatedArtist("Test Artist");
        List<EventDTO> events = Arrays.asList(event);
        when(eventServiceClient.searchEventsByArtist(eq("Test Event"), eq("Gateway-Service"), 
                anyString(), anyString())).thenReturn(events);
        when(eventServiceClient.filterEventsByArtist(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        // When
        ArtistWithEventsDTO result = artistService.scheduleEventForArtist(1L, "Test Event", "EU-RO", "ro-RO");

        // Then
        assertNotNull(result);
        verify(eventServiceClient).searchEventsByArtist(eq("Test Event"), eq("Gateway-Service"), 
                eq("EU-RO"), eq("ro-RO"));
    }

    @Test
    void testScheduleEventForArtist_EventNotFound() {
        // Given
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(eventServiceClient.searchEventsByArtist(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.emptyList());

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                artistService.scheduleEventForArtist(1L, "Non-existent Event", "EU-RO", "ro-RO"));
    }

    @Test
    void testScheduleEventForArtist_EventNotAssociated() {
        // Given
        when(artistRepository.findById(1L)).thenReturn(Optional.of(artist));

        EventDTO event = new EventDTO();
        event.setName("Test Event");
        event.setAssociatedArtist("Different Artist");
        List<EventDTO> events = Arrays.asList(event);
        when(eventServiceClient.searchEventsByArtist(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(events);

        // When & Then
        assertThrows(RuntimeException.class, () -> 
                artistService.scheduleEventForArtist(1L, "Test Event", "EU-RO", "ro-RO"));
    }
}
