package com.example.raceapp.controller;

import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.service.PilotService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing pilots.
 */
@RestController
@RequestMapping("/pilots")
public class PilotController {
    private final PilotService pilotService;

    /**
     * Constructs a PilotController with the provided PilotService.
     *
     * @param pilotService the service for managing pilot operations.
     */
    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    /**
     * Creates a new pilot.
     *
     * @param pilotDto Dto containing pilot data.
     * @return Created pilot Dto with HTTP 201.
     */
    @PostMapping
    public ResponseEntity<PilotResponse> createPilot(@RequestBody PilotDto pilotDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilotDto));
    }

    /**
     * Retrieves pilots by optional filters.
     *
     * @param name Pilot name filter.
     * @param age Pilot age filter.
     * @param experience Pilot experience filter.
     * @return List of filtered pilots.
     */
    @GetMapping
    public ResponseEntity<List<PilotResponse>> getPilots(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer experience) {
        return ResponseEntity.ok(pilotService.searchPilots(name, age, experience));
    }

    /**
     * Retrieves a pilot by ID.
     *
     * @param id Pilot ID.
     * @return Pilot Dto or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PilotResponse> getPilotById(@PathVariable Long id) {
        Optional<PilotResponse> pilot = pilotService.getPilotById(id);
        return pilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/by-car-brand")
    public ResponseEntity<Page<PilotResponse>> getByCarBrand(
            @RequestParam String brand,
            Pageable pageable) {
        return ResponseEntity.ok(pilotService.getPilotsByCarBrand(brand, pageable));
    }

    /**
     * Updates an existing pilot.
     *
     * @param id       Pilot ID.
     * @param pilotDto Updated pilot data.
     * @return Updated pilot Dto or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PilotResponse> updatePilot(@PathVariable Long id,
                                                     @RequestBody PilotDto pilotDto) {
        Optional<PilotResponse> updatedPilot = pilotService.updatePilot(id, pilotDto);
        return updatedPilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity
                                                                    .notFound().build());
    }

    /**
     * Partially updates a pilot by ID.
     *
     * @param id      the ID of the pilot to update.
     * @param updates map of fields to update.
     * @return updated pilot DTO, or 404 if not found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<PilotResponse> partialUpdatePilot(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Optional<PilotResponse> updatedPilot = pilotService.partialUpdatePilot(id, updates);
        return updatedPilot
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a pilot by ID.
     *
     * @param id Pilot ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}