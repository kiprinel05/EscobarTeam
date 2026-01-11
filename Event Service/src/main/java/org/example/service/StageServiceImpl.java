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

@Service
@Transactional
public class StageServiceImpl implements IStageService {

    private final StageRepository stageRepository;
    private final StageMapper stageMapper;

    @Autowired
    public StageServiceImpl(StageRepository stageRepository, StageMapper stageMapper) {
        this.stageRepository = stageRepository;
        this.stageMapper = stageMapper;
    }

    @Override
    public List<StageDTO> getAllStages() {
        return stageRepository.findAll().stream()
                .map(stageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StageDTO getStageById(Long id) {
        Stage stage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scena cu ID " + id + " nu a fost gasita"));
        return stageMapper.toDTO(stage);
    }

    @Override
    public StageDTO createStage(StageDTO stageDTO) {
        Stage stage = stageMapper.toEntity(stageDTO);
        Stage savedStage = stageRepository.save(stage);
        return stageMapper.toDTO(savedStage);
    }

    @Override
    public StageDTO updateStage(Long id, StageDTO stageDTO) {
        Stage existingStage = stageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Scena cu ID " + id + " nu a fost gasita"));
        
        stageMapper.updateEntityFromDTO(stageDTO, existingStage);
        Stage updatedStage = stageRepository.save(existingStage);
        return stageMapper.toDTO(updatedStage);
    }

    @Override
    public void deleteStage(Long id) {
        if (!stageRepository.existsById(id)) {
            throw new RuntimeException("Scena cu ID " + id + " nu a fost gasita");
        }
        stageRepository.deleteById(id);
    }

    @Override
    public List<StageDTO> searchByName(String name) {
        return stageRepository.findByNameContainingIgnoreCase(name).stream()
                .map(stageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StageDTO> filterByLocation(String location) {
        return stageRepository.findByLocation(location).stream()
                .map(stageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<StageDTO> filterByMinCapacity(Integer minCapacity) {
        return stageRepository.findByMaxCapacityGreaterThanEqual(minCapacity).stream()
                .map(stageMapper::toDTO)
                .sorted((s1, s2) -> s2.getMaxCapacity().compareTo(s1.getMaxCapacity()))
                .collect(Collectors.toList());
    }
}

