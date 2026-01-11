package org.example.service;

import org.example.dto.ArtistCreateDTO;
import org.example.dto.ArtistDTO;
import org.example.entity.Artist;
import org.example.exception.ArtistNotFoundException;
import org.example.mapper.ArtistMapper;
import org.example.repository.ArtistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ArtistServiceImpl implements IArtistService {

    private final ArtistRepository artistRepository;
    private final ArtistMapper artistMapper;

    @Autowired
    public ArtistServiceImpl(ArtistRepository artistRepository, ArtistMapper artistMapper) {
        this.artistRepository = artistRepository;
        this.artistMapper = artistMapper;
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
}

