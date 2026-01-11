package org.example.mapper;

import org.example.dto.StageDTO;
import org.example.entity.Stage;
import org.springframework.stereotype.Component;

@Component
public class StageMapper {

    public StageDTO toDTO(Stage stage) {
        if (stage == null) {
            return null;
        }
        StageDTO dto = new StageDTO();
        dto.setId(stage.getId());
        dto.setName(stage.getName());
        dto.setLocation(stage.getLocation());
        dto.setMaxCapacity(stage.getMaxCapacity());
        return dto;
    }

    public Stage toEntity(StageDTO stageDTO) {
        if (stageDTO == null) {
            return null;
        }
        Stage stage = new Stage();
        stage.setId(stageDTO.getId());
        stage.setName(stageDTO.getName());
        stage.setLocation(stageDTO.getLocation());
        stage.setMaxCapacity(stageDTO.getMaxCapacity());
        return stage;
    }

    public void updateEntityFromDTO(StageDTO stageDTO, Stage stage) {
        if (stageDTO == null || stage == null) {
            return;
        }
        if (stageDTO.getName() != null) {
            stage.setName(stageDTO.getName());
        }
        if (stageDTO.getLocation() != null) {
            stage.setLocation(stageDTO.getLocation());
        }
        if (stageDTO.getMaxCapacity() != null) {
            stage.setMaxCapacity(stageDTO.getMaxCapacity());
        }
    }
}
