package com.example.raceapp.controller;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.service.RaceService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/race")
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @PostMapping("/add")
    public ResponseEntity<Race> addRace(@RequestBody Race race) {
        Race createdRace = raceService.saveRace(race);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRace);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Race>> getAllRaces() {
        List<Race> races = raceService.getAllRaces();
        return ResponseEntity.ok(races);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Race> getRaceById(@PathVariable Long id) {
        Race race = raceService.getRaceById(id);
        return ResponseEntity.ok(race);
    }

    @GetMapping("/{id}/pilots")
    public ResponseEntity<List<Pilot>> getPilotsForRace(@PathVariable Long id) {
        List<Pilot> pilots = raceService.getPilotsForRace(id);
        return ResponseEntity.ok(pilots);
    }
}
