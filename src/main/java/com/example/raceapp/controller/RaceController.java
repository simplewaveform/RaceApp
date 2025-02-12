package com.example.raceapp.controller;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.service.RaceService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class for managing Race-related operations.
 * Provides endpoints to add, retrieve, and get pilots for races.
 */
@RestController
@RequestMapping("/race")
public class RaceController {

    private final RaceService raceService;

    /**
     * Constructor to inject the RaceService.
     *
     * @param raceService the service used to handle race-related logic.
     */
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * Endpoint to add a new race.
     *
     * @param race the race object to be created.
     * @return the created race object along with a CREATED status.
     */
    @PostMapping("/add")
    public ResponseEntity<Race> addRace(@RequestBody Race race) {
        Race createdRace = raceService.saveRace(race);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRace);
    }

    /**
     * Endpoint to retrieve all races.
     *
     * @return a list of all races.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Race>> getAllRaces() {
        List<Race> races = raceService.getAllRaces();
        return ResponseEntity.ok(races);
    }

    /**
     * Endpoint to retrieve a race by its ID.
     *
     * @param id the ID of the race to be retrieved.
     * @return the race object with the given ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Race> getRaceById(@PathVariable Long id) {
        Race race = raceService.getRaceById(id);
        return ResponseEntity.ok(race);
    }

    /**
     * Endpoint to retrieve all pilots participating in a specific race.
     *
     * @param id the ID of the race to get pilots for.
     * @return a list of pilots participating in the specified race.
     */
    @GetMapping("/{id}/pilots")
    public ResponseEntity<List<Pilot>> getPilotsForRace(@PathVariable Long id) {
        List<Pilot> pilots = raceService.getPilotsForRace(id);
        return ResponseEntity.ok(pilots);
    }
}
