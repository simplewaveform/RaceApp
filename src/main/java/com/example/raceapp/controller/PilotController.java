package com.example.raceapp.controller;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.service.PilotService;
import java.util.List;
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
 * REST controller for managing Pilot entities.
 * Provides endpoints for creating, retrieving, updating, and deleting pilots.
 */
@RestController
@RequestMapping("/pilots")
public class PilotController {
    private final PilotService pilotService;

    /**
     * Constructs a new PilotController with the specified PilotService.
     *
     * @param pilotService The service responsible for handling pilot-related operations.
     */
    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    /**
     * Creates a new pilot.
     * Accepts a JSON representation of a pilot and delegates the creation process to the
     * PilotService.
     *
     * @param pilot The pilot object to be created, provided in the request body.
     * @return A ResponseEntity containing the created pilot
     *          and an HTTP status code of 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Pilot> createPilot(@RequestBody Pilot pilot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilot));
    }

    /**
     * Retrieves a pilot by its unique identifier.
     *
     * @param id The unique identifier of the pilot to retrieve.
     * @return A ResponseEntity containing the pilot if found, or an empty response if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id) {
        return ResponseEntity.of(pilotService.getPilotById(id));
    }

    /**
     * Searches for pilots based on optional query parameters.
     * Supports filtering by name, age, and experience.
     *
     * @param name      (Optional) The name of the pilot to filter by.
     * @param age       (Optional) The age of the pilot to filter by.
     * @param experience (Optional) The experience of the pilot to filter by.
     * @return A ResponseEntity containing a list of pilots matching the search criteria.
     */
    @GetMapping
    public ResponseEntity<List<Pilot>> getPilots(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer experience) {
        List<Pilot> pilots = pilotService.searchPilots(name, age, experience);
        return ResponseEntity.ok(pilots);
    }

    /**
     * Updates an existing pilot by replacing its data with the provided pilot object.
     * The pilot ID in the path must match the ID of the pilot being updated.
     *
     * @param id     The unique identifier of the pilot to update.
     * @param pilot  The updated pilot object, provided in the request body.
     * @return A ResponseEntity containing the updated pilot if successful, or an empty response
     *         if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Pilot> updatePilot(@PathVariable Long id, @RequestBody Pilot pilot) {
        return ResponseEntity.of(pilotService.updatePilot(id, pilot));
    }

    /**
     * Partially updates an existing pilot.
     * Only the fields present in the provided pilot object will be updated.
     *
     * @param id     The unique identifier of the pilot to partially update.
     * @param pilot  The partial pilot object containing updated
     *               fields, provided in the request body.
     * @return A ResponseEntity containing the updated pilot if successful, or an empty response
     *         if not found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Pilot> partialUpdatePilot(@PathVariable Long id,
                                                    @RequestBody Pilot pilot) {
        return ResponseEntity.of(pilotService.partialUpdatePilot(id, pilot));
    }

    /**
     * Deletes a pilot by its unique identifier.
     *
     * @param id The unique identifier of the pilot to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}