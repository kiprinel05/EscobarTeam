package org.example.repository;

import org.example.entity.Stage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StageRepository extends JpaRepository<Stage, Long> {
    
    Optional<Stage> findByName(String name);
    
    List<Stage> findByNameContainingIgnoreCase(String name);
    
    List<Stage> findByLocation(String location);
    
    List<Stage> findByMaxCapacityGreaterThanEqual(Integer maxCapacity);
}

