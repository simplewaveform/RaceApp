package com.example.raceapp.controller;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.service.PilotService;
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
@RequestMapping("/pilots")
public class PilotController {
    private final PilotService pilotService;

    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    @PostMapping
    public ResponseEntity<Pilot> createPilot(@RequestBody Pilot pilot) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilot));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id) {
        return ResponseEntity.of(pilotService.getPilotById(id));
    }

    @GetMapping
    public ResponseEntity<List<Pilot>> getPilots(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer age,
            @RequestParam(required = false) Integer experience) {
        List<Pilot> pilots = pilotService.searchPilots(name, age, experience);
        return ResponseEntity.ok(pilots);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Pilot> updatePilot(@PathVariable Long id, @RequestBody Pilot pilot) {
        return ResponseEntity.of(pilotService.updatePilot(id, pilot));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Pilot> partialUpdatePilot(@PathVariable Long id, @RequestBody Pilot pilot) {
        return ResponseEntity.of(pilotService.partialUpdatePilot(id, pilot));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}