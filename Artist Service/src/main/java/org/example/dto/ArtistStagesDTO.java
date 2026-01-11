package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArtistStagesDTO {
    private Long artistId;
    private String artistName;
    private List<StageInfoDTO> stages;
    private Integer totalStages;
}

