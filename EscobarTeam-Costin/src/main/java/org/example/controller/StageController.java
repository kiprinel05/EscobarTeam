package org.example.controller;

import jakarta.validation.Valid;
import org.example.dto.StageDTO;
import org.example.service.IStageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stages")
@CrossOrigin(origins = "*")
public class StageController {

    private final IStageService stageService;

    @Autowired
    public StageController(IStageService stageService) {
        this.stageService = stageService;
    }

    @GetMapping
    public ResponseEntity<List<StageDTO>> getAllStages() {
        List<StageDTO> stages = stageService.getAllStages();
        return ResponseEntity.ok(stages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StageDTO> getStageById(@PathVariable Long id) {
        StageDTO stage = stageService.getStageById(id);
        return ResponseEntity.ok(stage);
    }

    @PostMapping
    public ResponseEntity<StageDTO> createStage(@Valid @RequestBody StageDTO stageDTO) {
        StageDTO createdStage = stageService.createStage(stageDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStage);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StageDTO> updateStage(@PathVariable Long id, @Valid @RequestBody StageDTO stageDTO) {
        StageDTO updatedStage = stageService.updateStage(id, stageDTO);
        return ResponseEntity.ok(updatedStage);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStage(@PathVariable Long id) {
        stageService.deleteStage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search/name")
    public ResponseEntity<List<StageDTO>> searchByName(@RequestParam String name) {
        List<StageDTO> stages = stageService.searchByName(name);
        return ResponseEntity.ok(stages);
    }

    @GetMapping("/filter/location")
    public ResponseEntity<List<StageDTO>> filterByLocation(@RequestParam String location) {
        List<StageDTO> stages = stageService.filterByLocation(location);
        return ResponseEntity.ok(stages);
    }

    @GetMapping("/filter/capacity")
    public ResponseEntity<List<StageDTO>> filterByMinCapacity(@RequestParam Integer minCapacity) {
        List<StageDTO> stages = stageService.filterByMinCapacity(minCapacity);
        return ResponseEntity.ok(stages);
    }
}
