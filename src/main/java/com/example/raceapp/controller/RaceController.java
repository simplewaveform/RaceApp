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

@RestController
@RequestMapping("/races")
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @PostMapping
    public ResponseEntity<Race> createRace(@RequestBody Race race) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(race));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Race> getRaceById(@PathVariable Long id) {
        return ResponseEntity.of(raceService.getRaceById(id));
    }

    @GetMapping
    public ResponseEntity<List<Race>> getRaces(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer year) {
        List<Race> races = raceService.searchRaces(name, year);
        return ResponseEntity.ok(races);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Race> updateRace(@PathVariable Long id, @RequestBody Race race) {
        return ResponseEntity.of(raceService.updateRace(id, race));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Race> partialUpdateRace(@PathVariable Long id, @RequestBody Race race) {
        return ResponseEntity.of(raceService.partialUpdateRace(id, race));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }
}