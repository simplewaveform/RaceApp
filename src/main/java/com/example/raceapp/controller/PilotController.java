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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing Pilot entities.
 * Provides CRUD operations for Pilot.
 */
@RestController
@RequestMapping("/pilots")
public class PilotController {
    private final PilotService pilotService;

    /**
     * Constructor for the PilotController class.
     *
     * @param pilotService The PilotService instance to be injected into this controller.
     */
    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    /**
     * Creates a new pilot entity.
     *
     * @param pilot the pilot object received in the request body.
     * @return the created pilot with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Pilot> createPilot(@RequestBody Pilot pilot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilot));
    }

    /**
     * Retrieves a pilot by its ID.
     *
     * @param id the ID of the pilot.
     * @return the pilot if found, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id) {
        return ResponseEntity.of(pilotService.getPilotById(id));
    }

    /**
     * Retrieves all pilots.
     *
     * @return a list of all pilots.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Pilot>> getAllPilots() {
        return ResponseEntity.ok(pilotService.getAllPilots());
    }

    /**
     * Fully updates a pilot entity by its ID.
     *
     * @param id the ID of the pilot to update.
     * @param pilot the updated pilot data.
     * @return the updated pilot entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Pilot> updatePilot(@PathVariable Long id, @RequestBody Pilot pilot) {
        return ResponseEntity.of(pilotService.updatePilot(id, pilot));
    }

    /**
     * Partially updates a pilot entity by its ID.
     *
     * @param id the ID of the pilot to update.
     * @param pilot the partial pilot data.
     * @return the updated pilot entity.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Pilot> partialUpdatePilot(@PathVariable Long id,
                                                    @RequestBody Pilot pilot) {
        return ResponseEntity.of(pilotService.partialUpdatePilot(id, pilot));
    }

    /**
     * Deletes a pilot by its ID.
     *
     * @param id the ID of the pilot to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}