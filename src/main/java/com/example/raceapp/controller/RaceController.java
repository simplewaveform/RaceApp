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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing Race entities.
 * Provides CRUD operations for Race.
 */
@RestController
@RequestMapping("/races")
public class RaceController {
    private final RaceService raceService;

    /**
     * Constructor for the RaceController class.
     *
     * @param raceService The RaceService instance to be injected into this controller.
     */
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * Creates a new race entity.
     *
     * @param race the race object received in the request body.
     * @return the created race with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Race> createRace(@RequestBody Race race) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(race));
    }

    /**
     * Retrieves a race by its ID.
     *
     * @param id the ID of the race.
     * @return the race if found, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Race> getRaceById(@PathVariable Long id) {
        return ResponseEntity.of(raceService.getRaceById(id));
    }

    /**
     * Retrieves all races with their associated pilots and cars.
     *
     * @return a list of all races.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Race>> getAllRaces() {
        List<Race> races = raceService.getAllRaces();
        // Manually fetching associated entities to ensure they are included in the response
        races.forEach(race -> {
            race.getPilots().size(); // Initialize lazy-loaded collection
            race.getCars().size(); // Initialize lazy-loaded collection
        });
        return ResponseEntity.ok(races);
    }

    /**
     * Fully updates a race entity by its ID.
     *
     * @param id the ID of the race to update.
     * @param race the updated race data.
     * @return the updated race entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Race> updateRace(@PathVariable Long id, @RequestBody Race race) {
        return ResponseEntity.of(raceService.updateRace(id, race));
    }

    /**
     * Partially updates a race entity by its ID.
     *
     * @param id the ID of the race to update.
     * @param race the partial race data.
     * @return the updated race entity.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Race> partialUpdateRace(@PathVariable Long id, @RequestBody Race race) {
        return ResponseEntity.of(raceService.partialUpdateRace(id, race));
    }

    /**
     * Deletes a race by its ID.
     *
     * @param id the ID of the race to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }
}