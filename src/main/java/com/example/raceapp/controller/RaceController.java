package com.example.raceapp.controller;

import com.example.raceapp.dto.RaceDTO;
import com.example.raceapp.service.RaceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing races.
 */
@RestController
@RequestMapping("/races")
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * Creates a new race.
     * @param raceDTO DTO containing race data.
     * @return Created race DTO with HTTP 201.
     */
    @PostMapping
    public ResponseEntity<RaceDTO> createRace(@RequestBody RaceDTO raceDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(raceDTO));
    }

    /**
     * Retrieves all races.
     * @return List of all races.
     */
    @GetMapping
    public ResponseEntity<List<RaceDTO>> getAllRaces() {
        return ResponseEntity.ok(raceService.getAllRaces());
    }

    /**
     * Retrieves a race by ID.
     * @param id Race ID.
     * @return Race DTO or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<RaceDTO> getRaceById(@PathVariable Long id) {
        Optional<RaceDTO> race = raceService.getRaceById(id);
        return race.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing race.
     * @param id Race ID.
     * @param raceDTO Updated race data.
     * @return Updated race DTO or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<RaceDTO> updateRace(@PathVariable Long id, @RequestBody RaceDTO raceDTO) {
        Optional<RaceDTO> updatedRace = raceService.updateRace(id, raceDTO);
        return updatedRace.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a race by ID.
     * @param id Race ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }
}