package com.example.raceapp.controller;

import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.service.RaceService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * REST controller for managing races.
 */
@RestController
@RequestMapping("/races")
public class RaceController {
    private final RaceService raceService;

    /**
     * Constructs a RaceController with the provided RaceService.
     *
     * @param raceService the service for managing race operations.
     */
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * Creates a new race.
     *
     * @param raceDto Dto containing race data.
     * @return Created race Dto with HTTP 201.
     */
    @PostMapping
    public ResponseEntity<RaceDto> createRace(@RequestBody RaceDto raceDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(raceDto));
    }

    /**
     * Retrieves all races.
     *
     * @return List of all races.
     */
    @GetMapping
    public ResponseEntity<List<RaceDto>> getAllRaces() {
        return ResponseEntity.ok(raceService.getAllRaces());
    }

    /**
     * Retrieves a race by ID.
     *
     * @param id Race ID.
     * @return Race Dto or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RaceDto> getRaceById(@PathVariable Long id) {
        Optional<RaceDto> race = raceService.getRaceById(id);
        return race.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing race.
     *
     * @param id Race ID.
     * @param raceDto Updated race data.
     * @return Updated race Dto or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RaceDto> updateRace(@PathVariable Long id, @RequestBody RaceDto raceDto) {
        Optional<RaceDto> updatedRace = raceService.updateRace(id, raceDto);
        return updatedRace.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity
                                                                   .notFound().build());
    }

    /**
     * Partially updates a race by ID.
     *
     * @param id      the ID of the race to update.
     * @param updates map of fields to update.
     * @return updated race DTO, or 404 if not found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<RaceDto> partialUpdateRace(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Optional<RaceDto> updatedRace = raceService.partialUpdateRace(id, updates);
        return updatedRace
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a race by ID.
     *
     * @param id Race ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }
}