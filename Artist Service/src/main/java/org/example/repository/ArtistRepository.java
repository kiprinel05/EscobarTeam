package org.example.repository;

import org.example.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {

    Optional<Artist> findByName(String name);
    List<Artist> findByNameContainingIgnoreCase(String name);
    List<Artist> findByGenre(String genre);
    List<Artist> findByNationality(String nationality);
    List<Artist> findByIsActiveTrue();
    @Query("SELECT a FROM Artist a WHERE a.rating >= :minRating")
    List<Artist> findByRatingGreaterThanEqual(@Param("minRating") Double minRating);
    @Query("SELECT a FROM Artist a WHERE a.age BETWEEN :minAge AND :maxAge")
    List<Artist> findByAgeBetween(@Param("minAge") Integer minAge, @Param("maxAge") Integer maxAge);
    List<Artist> findByGenreAndNationality(String genre, String nationality);
}

