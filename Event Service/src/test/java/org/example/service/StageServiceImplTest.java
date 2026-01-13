package org.example.service;

import org.example.dto.StageDTO;
import org.example.entity.Stage;
import org.example.mapper.StageMapper;
import org.example.repository.StageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StageServiceImplTest {

    @Mock
    private StageRepository stageRepository;

    @Mock
    private StageMapper stageMapper;

    @InjectMocks
    private StageServiceImpl stageService;

    private Stage stage;
    private StageDTO stageDTO;

    @BeforeEach
    void setUp() {
        stage = new Stage();
        stage.setId(1L);
        stage.setName("Main Stage");
        stage.setLocation("Arena A");
        stage.setMaxCapacity(5000);

        stageDTO = new StageDTO();
        stageDTO.setId(1L);
        stageDTO.setName("Main Stage");
        stageDTO.setLocation("Arena A");
        stageDTO.setMaxCapacity(5000);
    }

    @Test
    void testGetAllStages_Success() {
        // Given
        when(stageRepository.findAll()).thenReturn(Arrays.asList(stage));
        when(stageMapper.toDTO(stage)).thenReturn(stageDTO);

        // When
        List<StageDTO> result = stageService.getAllStages();

        // Then
        assertEquals(1, result.size());
        verify(stageRepository).findAll();
    }

    @Test
    void testGetAllStages_Empty() {
        // Given
        when(stageRepository.findAll()).thenReturn(Collections.emptyList());

        // When
        List<StageDTO> result = stageService.getAllStages();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void testGetStageById_Success() {
        // Given
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(stageMapper.toDTO(stage)).thenReturn(stageDTO);

        // When
        StageDTO result = stageService.getStageById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void testGetStageById_NotFound() {
        // Given
        when(stageRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> stageService.getStageById(99L));
    }

    @Test
    void testCreateStage_Success() {
        // Given
        when(stageMapper.toEntity(stageDTO)).thenReturn(stage);
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toDTO(stage)).thenReturn(stageDTO);

        // When
        StageDTO result = stageService.createStage(stageDTO);

        // Then
        assertNotNull(result);
        verify(stageRepository).save(any(Stage.class));
    }

    @Test
    void testUpdateStage_Success() {
        // Given
        when(stageRepository.findById(1L)).thenReturn(Optional.of(stage));
        when(stageRepository.save(stage)).thenReturn(stage);
        when(stageMapper.toDTO(stage)).thenReturn(stageDTO);

        // When
        StageDTO result = stageService.updateStage(1L, stageDTO);

        // Then
        assertNotNull(result);
        verify(stageMapper).updateEntityFromDTO(eq(stageDTO), eq(stage));
    }

    @Test
    void testUpdateStage_NotFound() {
        // Given
        when(stageRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(RuntimeException.class, () -> stageService.updateStage(99L, stageDTO));
    }

    @Test
    void testDeleteStage_Success() {
        // Given
        when(stageRepository.existsById(1L)).thenReturn(true);
        doNothing().when(stageRepository).deleteById(1L);

        // When
        assertDoesNotThrow(() -> stageService.deleteStage(1L));

        // Then
        verify(stageRepository).deleteById(1L);
    }

    @Test
    void testDeleteStage_NotFound() {
        // Given
        when(stageRepository.existsById(99L)).thenReturn(false);

        // When & Then
        assertThrows(RuntimeException.class, () -> stageService.deleteStage(99L));
    }

    @Test
    void testSearchByName_Success() {
        // Given
        when(stageRepository.findByNameContainingIgnoreCase("Main"))
                .thenReturn(Collections.singletonList(stage));
        when(stageMapper.toDTO(stage)).thenReturn(stageDTO);

        // When
        List<StageDTO> result = stageService.searchByName("Main");

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testFilterByLocation_Success() {
        // Given
        when(stageRepository.findByLocation("Arena A"))
                .thenReturn(Collections.singletonList(stage));
        when(stageMapper.toDTO(stage)).thenReturn(stageDTO);

        // When
        List<StageDTO> result = stageService.filterByLocation("Arena A");

        // Then
        assertEquals(1, result.size());
    }

    @Test
    void testFilterByMinCapacity_Success() {
        // Given
        Stage stage2 = new Stage();
        stage2.setId(2L);
        stage2.setMaxCapacity(3000);

        StageDTO dto2 = new StageDTO();
        dto2.setId(2L);
        dto2.setMaxCapacity(3000);

        when(stageRepository.findByMaxCapacityGreaterThanEqual(2000))
                .thenReturn(Arrays.asList(stage2, stage));
        when(stageMapper.toDTO(stage)).thenReturn(stageDTO);
        when(stageMapper.toDTO(stage2)).thenReturn(dto2);

        // When
        List<StageDTO> result = stageService.filterByMinCapacity(2000);

        // Then
        assertEquals(2, result.size());
        // Should be sorted descending by capacity
        assertTrue(result.get(0).getMaxCapacity() >= result.get(1).getMaxCapacity());
    }
}
