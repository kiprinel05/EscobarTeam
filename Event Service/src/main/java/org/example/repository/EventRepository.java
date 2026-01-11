package org.example.repository;

import org.example.entity.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    
    List<Event> findByNameContainingIgnoreCase(String name);
    
    List<Event> findByAssociatedArtistContainingIgnoreCase(String artist);
    
    List<Event> findByStageId(Long stageId);
    
    List<Event> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    List<Event> findByCapacityGreaterThanEqual(Integer capacity);
    
    @Query("SELECT e FROM Event e WHERE e.stage.id = :stageId AND e.date = :date")
    List<Event> findByStageAndDate(@Param("stageId") Long stageId, @Param("date") LocalDateTime date);
    
    @Query(value = "SELECT * FROM event WHERE stage_id = :stageId AND " +
           "((date >= :startDate AND date < :endDate) OR " +
           "(date < :startDate AND date + INTERVAL '2 hours' > :startDate))", 
           nativeQuery = true)
    List<Event> findConflictingEvents(@Param("stageId") Long stageId, 
                                      @Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT e FROM Event e WHERE e.stage.id = :stageId ORDER BY e.date ASC")
    List<Event> findAllByStageIdOrderByDate(@Param("stageId") Long stageId);
    
    @Query(value = "SELECT * FROM event WHERE DATE(date) = DATE(:date)", nativeQuery = true)
    List<Event> findByDate(@Param("date") LocalDateTime date);
}

