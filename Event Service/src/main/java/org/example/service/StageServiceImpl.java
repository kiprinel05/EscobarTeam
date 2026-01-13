package org.example.service;

import org.example.dto.StageDTO;
import org.example.entity.Stage;
import org.example.mapper.StageMapper;
import org.example.repository.StageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service implementation for managing stages in the festival management system.
 * Provides business logic for CRUD operations, searching, and filtering stages.
 *
 * @author EscobarTeam
 */
@Service
@Transactional
public class StageServiceImpl implements IStageService {

    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    /**
     * Constructs a new {@code StageServiceImpl} with the required dependencies.
     *
     * @param stageRepository the repository for stage data access
     * @param stageMapper the mapper for converting between entities and DTOs
     */
    @Autowired
    public StageServiceImpl(StageRepository stageRepository, StageMapper stageMapper) {
        this.stageRepository = stageRepository;
        this.stageMapper = stageMapper;
    }

    /**
     * Retrieves all stages from the database.
     *
     * @return a list of all {@code StageDTO} objects representing all stages
     */
    @Override
    public List<StageDTO> getAllStages() {
        return stageRepository.findAll().stream()
                .map(stageMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a stage by its unique identifier.
     *
     * @param id the unique identifier of the stage to retrieve
     * @return the {@code StageDTO} object representing the stage with the specified ID
     * @throws RuntimeException if no stage exists with the given ID
     */
    @Override
    public StageDTO getStageById(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scena cu ID " + id + " nu a fost gasita"));
        return stageMapper.toDTO(stage);
    }

    /**
     * Creates a new stage in the system.
     *
     * @param stageDTO the DTO containing the data for the new stage
     * @return the {@code StageDTO} object representing the newly created stage
     */
    @Override
    public StageDTO createStage(StageDTO stageDTO) {
        Stage stage = stageMapper.toEntity(stageDTO);
        Stage savedStage = stageRepository.save(stage);
        return stageMapper.toDTO(savedStage);
    }

    /**
     * Updates an existing stage with new information.
     *
     * @param id the unique identifier of the stage to update
     * @param stageDTO the DTO containing the updated stage information
     * @return the {@code StageDTO} object representing the updated stage
     * @throws RuntimeException if no stage exists with the given ID
     */
    @Override
    public StageDTO updateStage(Long id, StageDTO stageDTO) {
        Stage existingStage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scena cu ID " + id + " nu a fost gasita"));
        
        stageMapper.updateEntityFromDTO(stageDTO, existingStage);
        Stage updatedStage = stageRepository.save(existingStage);
        return stageMapper.toDTO(updatedStage);
    }

    /**
     * Deletes a stage from the system by its unique identifier.
     *
     * @param id the unique identifier of the stage to delete
     * @throws RuntimeException if no stage exists with the given ID
     */
    @Override
    public void deleteStage(Long id) {
        if (!stageRepository.existsById(id)) {
            throw new RuntimeException("Scena cu ID " + id + " nu a fost gasita");
        }
        stageRepository.deleteById(id);
    }

    /**
     * Searches for stages whose names contain the specified search string.
     * The search is case-insensitive.
     *
     * @param name the search string to match against stage names
     * @return a list of {@code StageDTO} objects matching the search criteria
     */
    @Override
    public List<StageDTO> searchByName(String name) {
        return stageRepository.findByNameContainingIgnoreCase(name).stream()
                .map(stageMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters stages by their location.
     *
     * @param location the location to filter by
     * @return a list of {@code StageDTO} objects representing stages at the specified location
     */
    @Override
    public List<StageDTO> filterByLocation(String location) {
        return stageRepository.findByLocation(location).stream()
                .map(stageMapper::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Filters stages by minimum capacity threshold.
     * Results are sorted by capacity in descending order.
     *
     * @param minCapacity the minimum capacity value to filter by
     * @return a list of {@code StageDTO} objects representing stages with capacity greater than or equal to the specified value
     */
    @Override
    public List<StageDTO> filterByMinCapacity(Integer minCapacity) {
        return stageRepository.findByMaxCapacityGreaterThanEqual(minCapacity).stream()
                .map(stageMapper::toDTO)
                .sorted((s1, s2) -> s2.getMaxCapacity().compareTo(s1.getMaxCapacity()))
                .collect(Collectors.toList());
    }
}

