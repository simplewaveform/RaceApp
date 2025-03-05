package com.example.raceapp.controller;

import com.example.raceapp.dto.PilotDTO;
import com.example.raceapp.service.PilotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing pilots.
 */
@RestController
@RequestMapping("/pilots")
public class PilotController {
    private final PilotService pilotService;

    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    /**
     * Creates a new pilot.
     * @param pilotDTO DTO containing pilot data.
     * @return Created pilot DTO with HTTP 201.
     */
    @PostMapping
    public ResponseEntity<PilotDTO> createPilot(@RequestBody PilotDTO pilotDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilotDTO));
    }

    /**
     * Retrieves pilots by optional filters.
     * @param name Pilot name filter.
     * @param age Pilot age filter.
     * @param experience Pilot experience filter.
     * @return List of filtered pilots.
     */
    @GetMapping
    public ResponseEntity<List<PilotDTO>> getPilots(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer experience) {
        return ResponseEntity.ok(pilotService.searchPilots(name, age, experience));
    }

    /**
     * Retrieves a pilot by ID.
     * @param id Pilot ID.
     * @return Pilot DTO or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PilotDTO> getPilotById(@PathVariable Long id) {
        Optional<PilotDTO> pilot = pilotService.getPilotById(id);
        return pilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing pilot.
     * @param id Pilot ID.
     * @param pilotDTO Updated pilot data.
     * @return Updated pilot DTO or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PilotDTO> updatePilot(@PathVariable Long id, @RequestBody PilotDTO pilotDTO) {
        Optional<PilotDTO> updatedPilot = pilotService.updatePilot(id, pilotDTO);
        return updatedPilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a pilot by ID.
     * @param id Pilot ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}