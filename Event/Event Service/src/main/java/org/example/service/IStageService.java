package org.example.service;

import org.example.dto.StageDTO;
import java.util.List;

public interface IStageService {
    List<StageDTO> getAllStages();
    StageDTO getStageById(Long id);
    StageDTO createStage(StageDTO stageDTO);
    StageDTO updateStage(Long id, StageDTO stageDTO);
    void deleteStage(Long id);
    List<StageDTO> searchByName(String name);
    List<StageDTO> filterByLocation(String location);
    List<StageDTO> filterByMinCapacity(Integer minCapacity);
}


