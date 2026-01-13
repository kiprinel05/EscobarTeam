package org.example.service;

import org.example.dto.StageDTO;
import java.util.List;

/**
 * Service interface for managing stages in the festival management system.
 * Provides operations for CRUD operations, searching, and filtering stages.
 *
 * @author EscobarTeam
 */
public interface IStageService {
    /**
     * Retrieves all stages from the database.
     *
     * @return a list of all {@code StageDTO} objects representing all stages
     */
    List<StageDTO> getAllStages();
    
    /**
     * Retrieves a stage by its unique identifier.
     *
     * @param id the unique identifier of the stage to retrieve
     * @return the {@code StageDTO} object representing the stage with the specified ID
     * @throws RuntimeException if no stage exists with the given ID
     */
    StageDTO getStageById(Long id);
    
    /**
     * Creates a new stage in the system.
     *
     * @param stageDTO the DTO containing the data for the new stage
     * @return the {@code StageDTO} object representing the newly created stage
     */
    StageDTO createStage(StageDTO stageDTO);
    
    /**
     * Updates an existing stage with new information.
     *
     * @param id the unique identifier of the stage to update
     * @param stageDTO the DTO containing the updated stage information
     * @return the {@code StageDTO} object representing the updated stage
     * @throws RuntimeException if no stage exists with the given ID
     */
    StageDTO updateStage(Long id, StageDTO stageDTO);
    
    /**
     * Deletes a stage from the system by its unique identifier.
     *
     * @param id the unique identifier of the stage to delete
     * @throws RuntimeException if no stage exists with the given ID
     */
    void deleteStage(Long id);
    
    /**
     * Searches for stages whose names contain the specified search string.
     * The search is case-insensitive.
     *
     * @param name the search string to match against stage names
     * @return a list of {@code StageDTO} objects matching the search criteria
     */
    List<StageDTO> searchByName(String name);
    
    /**
     * Filters stages by their location.
     *
     * @param location the location to filter by
     * @return a list of {@code StageDTO} objects representing stages at the specified location
     */
    List<StageDTO> filterByLocation(String location);
    
    /**
     * Filters stages by minimum capacity threshold.
     * Results are sorted by capacity in descending order.
     *
     * @param minCapacity the minimum capacity value to filter by
     * @return a list of {@code StageDTO} objects representing stages with capacity greater than or equal to the specified value
     */
    List<StageDTO> filterByMinCapacity(Integer minCapacity);
}


