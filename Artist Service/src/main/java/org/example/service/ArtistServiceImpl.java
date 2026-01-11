package org.example.service;

import org.example.client.EventServiceClient;
import org.example.dto.*;
import org.example.entity.Artist;
import org.example.exception.ArtistNotFoundException;
import org.example.mapper.ArtistMapper;
import org.example.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

    /*
      sorteaza dupa nume
     */
    @Override
    public List<ArtistDTO> sortArtistsByName() {
        return artistRepository.findAll().stream()
                .sorted(Comparator.comparing(Artist::getName))
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    /*
      sorteaza artistii dupa rating
     */
    @Override
    public List<ArtistDTO> sortArtistsByRating() {
        return artistRepository.findAll().stream()
                .filter(artist -> artist.getRating() != null)
                .sorted(Comparator.comparing(Artist::getRating).reversed())
                .map(artistMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ArtistWithEventsDTO getArtistWithEvents(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException(artistId));
        
        ArtistDTO artistDTO = artistMapper.toDTO(artist);
        
        List<org.example.client.EventServiceClient.EventResponseDTO> events = eventServiceClient.getAllEvents();
        
        List<EventInfoDTO> artistEvents = events.stream()
                .filter(event -> event.getAssociatedArtist() != null && 
                        event.getAssociatedArtist().equalsIgnoreCase(artist.getName()))
                .map(event -> new EventInfoDTO(
                        event.getId(),
                        event.getName(),
                        event.getDate(),
                        event.getStageName(),
                        event.getCapacity()
                ))
                .collect(Collectors.toList());
        
        return new ArtistWithEventsDTO(artistDTO, artistEvents, artistEvents.size());
    }

    @Override
    public ArtistWithEventsDTO assignArtistToEvent(Long artistId, String eventName) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException(artistId));
        
        ArtistDTO artistDTO = artistMapper.toDTO(artist);
        
        List<org.example.client.EventServiceClient.EventResponseDTO> allEvents = eventServiceClient.getAllEvents();
        
        org.example.client.EventServiceClient.EventResponseDTO foundEvent = allEvents.stream()
                .filter(event -> event.getName().equalsIgnoreCase(eventName))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Evenimentul cu numele '" + eventName + "' nu a fost găsit"));
        
        List<EventInfoDTO> events = List.of(new EventInfoDTO(
                foundEvent.getId(),
                foundEvent.getName(),
                foundEvent.getDate(),
                foundEvent.getStageName(),
                foundEvent.getCapacity()
        ));
        
        return new ArtistWithEventsDTO(artistDTO, events, 1);
    }

    @Override
    public ArtistStagesDTO getArtistStages(Long artistId) {
        Artist artist = artistRepository.findById(artistId)
                .orElseThrow(() -> new ArtistNotFoundException(artistId));
        
        ArtistDTO artistDTO = artistMapper.toDTO(artist);
        
        List<org.example.client.EventServiceClient.EventResponseDTO> allEvents = eventServiceClient.getAllEvents();
        
        List<org.example.client.EventServiceClient.EventResponseDTO> artistEvents = allEvents.stream()
                .filter(event -> event.getAssociatedArtist() != null && 
                        event.getAssociatedArtist().equalsIgnoreCase(artist.getName()))
                .collect(Collectors.toList());
        
        Map<String, List<org.example.client.EventServiceClient.EventResponseDTO>> eventsByStage = 
                artistEvents.stream()
                        .collect(Collectors.groupingBy(
                                e -> e.getStageName() != null ? e.getStageName() : "Unknown"
                        ));
        
        List<StageInfoDTO> stages = eventsByStage.entrySet().stream()
                .map(entry -> {
                    String stageName = entry.getKey();
                    List<org.example.client.EventServiceClient.EventResponseDTO> stageEvents = entry.getValue();
                    
                    org.example.client.EventServiceClient.EventResponseDTO firstEvent = stageEvents.get(0);
                    
                    StageInfoDTO stageInfo = new StageInfoDTO();
                    stageInfo.setStageId(firstEvent.getStageId());
                    stageInfo.setStageName(stageName);
                    stageInfo.setLocation(null);
                    stageInfo.setCapacity(firstEvent.getCapacity());
                    stageInfo.setNumberOfEvents(stageEvents.size());
                    
                    return stageInfo;
                })
                .collect(Collectors.toList());
        
        return new ArtistStagesDTO(artistId, artist.getName(), stages, stages.size());
    }
}

