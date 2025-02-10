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

    // Constructor injection
    @Autowired
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    // GET-запрос с Query Parameters
    @GetMapping("/info")
    public List<Race> getRaceInfo(@RequestParam String name, @RequestParam int year) {
        // Возвращаем список с одной гонкой в формате JSON
        return List.of(new Race(1, name, year));
    }

    // GET-запрос с Path Parameters
    @GetMapping("/{id}")
    public Race getRaceById(@PathVariable int id) {
        return raceService.getRaceById(id);
    }
}
