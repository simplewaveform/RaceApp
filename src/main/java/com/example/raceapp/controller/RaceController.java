package com.example.raceapp.controller;

import com.example.raceapp.model.Race;
import com.example.raceapp.service.RaceService;
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
 * REST controller for managing Race entities.
 * Provides endpoints for creating, retrieving, updating, and deleting races.
 */
@RestController
@RequestMapping("/races")
public class RaceController {
    private final RaceService raceService;

    /**
     * Constructs a new RaceController with the specified RaceService.
     *
     * @param raceService The service responsible for handling race-related operations.
     */
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * Creates a new race.
     * Accepts a JSON representation of a race and delegates
     * the creation process to the RaceService.
     *
     * @param race The race object to be created, provided in the request body.
     * @return A ResponseEntity containing the created race
     *         and an HTTP status code of 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Race> createRace(@RequestBody Race race) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(race));
    }

    /**
     * Retrieves a race by its unique identifier.
     *
     * @param id The unique identifier of the race to retrieve.
     * @return A ResponseEntity containing the race if found, or an empty response if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Race> getRaceById(@PathVariable Long id) {
        return ResponseEntity.of(raceService.getRaceById(id));
    }

    /**
     * Searches for races based on optional query parameters.
     * Supports filtering by name and year.
     *
     * @param name (Optional) The name of the race to filter by.
     * @param year (Optional) The year of the race to filter by.
     * @return A ResponseEntity containing a list of races matching the search criteria.
     */
    @GetMapping
    public ResponseEntity<List<Race>> getRaces(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer year) {
        List<Race> races = raceService.searchRaces(name, year);
        return ResponseEntity.ok(races);
    }

    /**
     * Updates an existing race by replacing its data with the provided race object.
     * The race ID in the path must match the ID of the race being updated.
     *
     * @param id   The unique identifier of the race to update.
     * @param race The updated race object, provided in the request body.
     * @return A ResponseEntity containing the updated race if successful, or an empty response
     *         if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Race> updateRace(@PathVariable Long id, @RequestBody Race race) {
        return ResponseEntity.of(raceService.updateRace(id, race));
    }

    /**
     * Partially updates an existing race.
     * Only the fields present in the provided race object will be updated.
     *
     * @param id   The unique identifier of the race to partially update.
     * @param race The partial race object containing updated fields, provided in the request body.
     * @return A ResponseEntity containing the updated race if successful, or an empty response
     *         if not found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Race> partialUpdateRace(@PathVariable Long id, @RequestBody Race race) {
        return ResponseEntity.of(raceService.partialUpdateRace(id, race));
    }

    /**
     * Deletes a race by its unique identifier.
     *
     * @param id The unique identifier of the race to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }
}