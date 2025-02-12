package com.example.raceapp.controller;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.service.PilotService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing pilots.
 * Provides endpoints to create, retrieve, and delete pilots, as well as get a pilot's races.
 */
@RestController
@RequestMapping("/pilot")
public class PilotController {
    private final PilotService pilotService;

    /**
     * Constructor to inject the PilotService.
     *
     * @param pilotService the service to manage pilot data
     */
    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    /**
     * Endpoint to create a new pilot.
     *
     * @param pilot the pilot data to create
     * @return the created pilot with HTTP status 201 (Created)
     */
    @PostMapping("/add")
    public ResponseEntity<Pilot> createPilot(@RequestBody Pilot pilot) {
        Pilot createdPilot = pilotService.createPilot(pilot);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPilot);
    }

    /**
     * Endpoint to retrieve all pilots.
     *
     * @return a list of all pilots with HTTP status 200 (OK)
     */
    @GetMapping("/all")
    public ResponseEntity<List<Pilot>> getAllPilots() {
        List<Pilot> pilots = pilotService.getAllPilots();
        return ResponseEntity.ok(pilots);
    }

    /**
     * Endpoint to retrieve a pilot by their ID.
     *
     * @param id the ID of the pilot
     * @return the pilot with the specified ID, or 404 if not found
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id) {
        Pilot pilot = pilotService.getPilotById(id);
        if (pilot != null) {
            return ResponseEntity.ok(pilot);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint to retrieve all races a specific pilot is participating in.
     *
     * @param id the ID of the pilot
     * @return a list of races the pilot is part of
     */
    @GetMapping("/{id}/races")
    public ResponseEntity<List<Race>> getRacesForPilot(@PathVariable Long id) {
        List<Race> races = pilotService.getRacesForPilot(id);
        return ResponseEntity.ok(races);
    }

    /**
     * Endpoint to delete a pilot by their ID.
     *
     * @param id the ID of the pilot to delete
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}