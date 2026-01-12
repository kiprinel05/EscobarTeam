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

@Service
@Transactional
public class ArtistServiceImpl implements IArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;
    private final EventServiceClient eventServiceClient;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository, ArtistMapper artistMapper, EventServiceClient eventServiceClient) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
        this.eventServiceClient = eventServiceClient;
    }

    @Override
    public List<ArtistDTO> getAllArtists() {
        return artistRepository.findAll().stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ArtistDTO getArtistById(Long id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
        return artistMapper.toDTO(artist);
    }

    @Override
    public ArtistDTO createArtist(ArtistCreateDTO artistCreateDTO) {
        Artist artist = artistMapper.toEntity(artistCreateDTO);
        Artist savedArtist = artistRepository.save(artist);
        return artistMapper.toDTO(savedArtist);
    }

    @Override
    public ArtistDTO updateArtist(Long id, ArtistDTO artistDTO) {
        Artist existingArtist = artistRepository.findById(id)
                .orElseThrow(() -> new ArtistNotFoundException(id));
        
        artistMapper.updateEntityFromDTO(artistDTO, existingArtist);
        Artist updatedArtist = artistRepository.save(existingArtist);
        return artistMapper.toDTO(updatedArtist);
    }

    @Override
    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new ArtistNotFoundException(id);
        }
        artistRepository.deleteById(id);
    }

    @Override
    public List<ArtistDTO> searchArtistsByName(String name) {
        return artistRepository.findByNameContainingIgnoreCase(name).stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArtistDTO> filterArtistsByGenre(String genre) {
        return artistRepository.findByGenre(genre).stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArtistDTO> filterArtistsByNationality(String nationality) {
        return artistRepository.findByNationality(nationality).stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArtistDTO> getActiveArtists() {
        return artistRepository.findByIsActiveTrue().stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArtistDTO> filterArtistsByMinRating(Double minRating) {
        return artistRepository.findByRatingGreaterThanEqual(minRating).stream()
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArtistDTO> sortArtistsByName() {
        return artistRepository.findAll().stream()
                .sorted(Comparator.comparing(Artist::getName))
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ArtistDTO> sortArtistsByRating() {
        return artistRepository.findAll().stream()
                .filter(artist -> artist.getRating() != null)
                .sorted(Comparator.comparing(Artist::getRating).reversed())
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

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

    private String getLocalizedMessage(String language, String artistName, int eventCount) {
        if ("en-US".equalsIgnoreCase(language) || "en".equalsIgnoreCase(language)) {
            return String.format("%s has %d upcoming event(s).", artistName, eventCount);
        } else {
            return String.format("%s are %d eveniment(e) programat(e).", artistName, eventCount);
        }
    }
}

