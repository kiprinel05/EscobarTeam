package org.example.mapper;

import org.example.dto.ArtistCreateDTO;
import org.example.dto.ArtistDTO;
import org.example.entity.Artist;
import org.springframework.stereotype.Component;

@Component
public class ArtistMapper {

    public ArtistDTO toDTO(Artist artist) {
        if (artist == null) {
            return null;
        }
        return ArtistDTO.builder()
                .id(artist.getId())
                .name(artist.getName())
                .genre(artist.getGenre())
                .age(artist.getAge())
                .nationality(artist.getNationality())
                .email(artist.getEmail())
                .biography(artist.getBiography())
                .rating(artist.getRating())
                .isActive(artist.getIsActive())
                .createdAt(artist.getCreatedAt())
                .updatedAt(artist.getUpdatedAt())
                .build();
    }

    public Artist toEntity(ArtistDTO artistDTO) {
        if (artistDTO == null) {
            return null;
        }
        Artist artist = new Artist();
        artist.setId(artistDTO.getId());
        artist.setName(artistDTO.getName());
        artist.setGenre(artistDTO.getGenre());
        artist.setAge(artistDTO.getAge());
        artist.setNationality(artistDTO.getNationality());
        artist.setEmail(artistDTO.getEmail());
        artist.setBiography(artistDTO.getBiography());
        artist.setRating(artistDTO.getRating());
        artist.setIsActive(artistDTO.getIsActive());
        return artist;
    }

    public Artist toEntity(ArtistCreateDTO artistCreateDTO) {
        if (artistCreateDTO == null) {
            return null;
        }
        Artist artist = new Artist();
        artist.setName(artistCreateDTO.getName());
        artist.setGenre(artistCreateDTO.getGenre());
        artist.setAge(artistCreateDTO.getAge());
        artist.setNationality(artistCreateDTO.getNationality());
        artist.setEmail(artistCreateDTO.getEmail());
        artist.setBiography(artistCreateDTO.getBiography());
        artist.setRating(artistCreateDTO.getRating());
        artist.setIsActive(true); // implicit activ la creare
        return artist;
    }

    public void updateEntityFromDTO(ArtistDTO artistDTO, Artist artist) {
        if (artistDTO == null || artist == null) {
            return;
        }
        if (artistDTO.getName() != null) {
            artist.setName(artistDTO.getName());
        }
        if (artistDTO.getGenre() != null) {
            artist.setGenre(artistDTO.getGenre());
        }
        if (artistDTO.getAge() != null) {
            artist.setAge(artistDTO.getAge());
        }
        if (artistDTO.getNationality() != null) {
            artist.setNationality(artistDTO.getNationality());
        }
        if (artistDTO.getEmail() != null) {
            artist.setEmail(artistDTO.getEmail());
        }
        if (artistDTO.getBiography() != null) {
            artist.setBiography(artistDTO.getBiography());
        }
        if (artistDTO.getRating() != null) {
            artist.setRating(artistDTO.getRating());
        }
        if (artistDTO.getIsActive() != null) {
            artist.setIsActive(artistDTO.getIsActive());
        }
    }
}

