package com.example.raceapp.controller;

import com.example.raceapp.model.Race;
import com.example.raceapp.service.RaceService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/race")
public class RaceController {
    private final RaceService raceService;

    @Autowired
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    // Получить гонку по ID (из базы)
    @GetMapping("/{id}")
    public Race getRaceById(@PathVariable Long id) {
        return raceService.getRaceById(id);
    }

    // Получить список всех гонок (из базы)
    @GetMapping("/all")
    public List<Race> getAllRaces() {
        return raceService.getAllRaces();
    }

    // Добавить гонку (в базу)
    @PostMapping("/add")
    public Race addRace(@RequestBody Race race) {
        return raceService.saveRace(race);
    }

}

