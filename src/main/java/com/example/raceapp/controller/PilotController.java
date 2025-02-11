package com.example.raceapp.controller;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.service.PilotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pilot")
public class PilotController {

    private final PilotService pilotService;

    @Autowired
    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    // 1. Добавление пилота
    @PostMapping("/add")
    public ResponseEntity<Pilot> createPilot(@RequestBody Pilot pilot) {
        Pilot createdPilot = pilotService.createPilot(pilot);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPilot);
    }

    // 2. Получение всех пилотов
    @GetMapping("/all")
    public ResponseEntity<List<Pilot>> getAllPilots() {
        List<Pilot> pilots = pilotService.getAllPilots();
        return ResponseEntity.ok(pilots);
    }

    // 3. Получение пилота по ID
    @GetMapping("/{id}")
    public ResponseEntity<Pilot> getPilotById(@PathVariable Long id) {
        Pilot pilot = pilotService.getPilotById(id);
        return ResponseEntity.ok(pilot);
    }

    // 4. Удаление пилота
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
        return ResponseEntity.noContent().build();
    }
}
