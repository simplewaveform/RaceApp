package com.example.raceapp.controller;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.service.PilotService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pilot")
public class PilotController {
    private final PilotService pilotService;

    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    @PostMapping("/add")
    public ResponseEntity<Pilot> createPilot(@RequestBody Pilot pilot) {
        Pilot createdPilot = pilotService.createPilot(pilot);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPilot);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Pilot>> getAllPilots() {
        List<Pilot> pilots = pilotService.getAllPilots();
        return ResponseEntity.ok(pilots);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id) {
        Pilot pilot = pilotService.getPilotById(id);
        return ResponseEntity.ok(pilot);
    }

    @GetMapping("/{id}/races")
    public ResponseEntity<List<Race>> getRacesForPilot(@PathVariable Long id) {
        List<Race> races = pilotService.getRacesForPilot(id);
        return ResponseEntity.ok(races);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
        return ResponseEntity.noContent().build();
    }
}