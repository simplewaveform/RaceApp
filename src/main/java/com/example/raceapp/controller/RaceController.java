package com.example.raceapp.controller;

import com.example.raceapp.model.Pilot;
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
import org.springframework.web.bind.annotation.ResponseStatus;
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
     * @return the race object with the given ID, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Race> getRaceById(@PathVariable Long id) {
        Race race = raceService.getRaceById(id);
        if (race != null) {
            return ResponseEntity.ok(race);
        } else {
            return ResponseEntity.notFound().build();
        }
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

    /**
     * Endpoint to delete a race by its ID.
     *
     * @param id the ID of the race to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }

    /**
     * Endpoint to add a pilot to a race.
     *
     * @param raceId the ID of the race.
     * @param pilotId the ID of the pilot to add.
     * @return the updated race entity.
     */
    @PostMapping("/{raceId}/pilot/{pilotId}")
    public ResponseEntity<Race> addPilotToRace(@PathVariable Long raceId,
                                               @PathVariable Long pilotId) {
        Race updatedRace = raceService.addPilotToRace(raceId, pilotId);
        if (updatedRace != null) {
            return ResponseEntity.ok(updatedRace);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint to fully update a race by its ID.
     *
     * @param id the ID of the race to update.
     * @param raceDetails the race details to update.
     * @return the updated race entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Race> updateRace(@PathVariable Long id, @RequestBody Race raceDetails) {
        Race race = raceService.getRaceById(id);
        if (race == null) {
            return ResponseEntity.notFound().build();
        }

        race.setName(raceDetails.getName());
        race.setYear(raceDetails.getYear());

        Race updatedRace = raceService.saveRace(race);
        return ResponseEntity.ok(updatedRace);
    }

    /**
     * Endpoint to partially update a race by its ID.
     *
     * @param id the ID of the race to update.
     * @param raceDetails the race details to update.
     * @return the updated race entity.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Race> partialUpdateRace(@PathVariable Long id,
                                                  @RequestBody Race raceDetails) {
        Race race = raceService.getRaceById(id);
        if (race == null) {
            return ResponseEntity.notFound().build();
        }

        if (raceDetails.getName() != null) {
            race.setName(raceDetails.getName());
        }
        if (raceDetails.getYear() != 0) {
            race.setYear(raceDetails.getYear());
        }

        Race updatedRace = raceService.saveRace(race);
        return ResponseEntity.ok(updatedRace);
    }
}