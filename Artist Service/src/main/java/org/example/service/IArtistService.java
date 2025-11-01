package org.example.service;

import org.example.dto.ArtistCreateDTO;
import org.example.dto.ArtistDTO;

import java.util.List;

public interface IArtistService {
    List<ArtistDTO> getAllArtists();
    ArtistDTO getArtistById(Long id);
    ArtistDTO createArtist(ArtistCreateDTO artistCreateDTO);
    ArtistDTO updateArtist(Long id, ArtistDTO artistDTO);
    void deleteArtist(Long id);
    List<ArtistDTO> searchArtistsByName(String name);
    List<ArtistDTO> filterArtistsByGenre(String genre);
    List<ArtistDTO> filterArtistsByNationality(String nationality);
    List<ArtistDTO> getActiveArtists();
    List<ArtistDTO> filterArtistsByMinRating(Double minRating);
    List<ArtistDTO> sortArtistsByName();
    List<ArtistDTO> sortArtistsByRating();
}

