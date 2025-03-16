package com.example.raceapp.controller;

import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.service.PilotService;
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
 * REST controller for managing pilots. Provides endpoints for creating, retrieving,
 * updating, partially updating, and deleting pilots. It also includes endpoints for
 * searching pilots by various filters and getting pilots associated with specific car brands.
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
     * @param pilotDto the DTO containing the pilot data to create.
     * @return a {@link ResponseEntity} containing the created pilot's data with HTTP status 201.
     */
    @PostMapping
    public ResponseEntity<PilotResponse> createPilot(@RequestBody PilotDto pilotDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilotDto));
    }

    /**
     * Retrieves pilots based on optional filters like name, age, and experience.
     * If no filters are provided, it retrieves all pilots.
     *
     * @param name the name filter for pilots (optional).
     * @param age the age filter for pilots (optional).
     * @param experience the experience filter for pilots (optional).
     * @return a {@link ResponseEntity} containing a list of {@link PilotResponse} DTOs.
     */
    @GetMapping
    public ResponseEntity<Page<PilotResponse>> getPilots(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer experience,
            Pageable pageable
    ) {
        return ResponseEntity.ok(pilotService.searchPilotsWithPagination(name,
                age, experience, pageable));
    }

    /**
     * Retrieves a specific pilot by their ID.
     *
     * @param id the ID of the pilot to retrieve.
     * @return a {@link ResponseEntity} containing the {@link PilotResponse} DTO of the pilot,
     *         or a 404 response if the pilot with the specified ID is not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<PilotResponse> getPilotById(@PathVariable Long id) {
        Optional<PilotResponse> pilot = pilotService.getPilotById(id);
        return pilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves pilots associated with a specific car brand.
     * Supports pagination for large result sets.
     *
     * @param brand the brand of the car to filter pilots by.
     * @param pageable pagination parameters for the result set.
     * @return a {@link ResponseEntity} containing a {@link Page} of {@link PilotResponse} DTOs.
     */
    @GetMapping("/by-car-brand")
    public ResponseEntity<Page<PilotResponse>> getByCarBrand(
            @RequestParam String brand,
            Pageable pageable) {
        return ResponseEntity.ok(pilotService.getPilotsByCarBrand(brand, pageable));
    }

    /**
     * Updates an existing pilot's details.
     *
     * @param id the ID of the pilot to update.
     * @param pilotDto the DTO containing the updated pilot data.
     * @return a {@link ResponseEntity} containing the updated
     *         pilot's data, or a 404 response if the pilot is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<PilotResponse> updatePilot(@PathVariable Long id,
                                                     @RequestBody PilotDto pilotDto) {
        Optional<PilotResponse> updatedPilot = pilotService.updatePilot(id, pilotDto);
        return updatedPilot.map(ResponseEntity::ok).orElseGet(()
                -> ResponseEntity.notFound().build());
    }

    /**
     * Partially updates a pilot's details based on provided fields.
     *
     * @param id the ID of the pilot to update.
     * @param updates a map of fields to update, with field names as keys and new values as values.
     * @return a {@link ResponseEntity} containing the updated pilot's data,
     *         or a 404 response if the pilot is not found.
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
     * Deletes a pilot by their ID.
     *
     * @param id the ID of the pilot to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}
