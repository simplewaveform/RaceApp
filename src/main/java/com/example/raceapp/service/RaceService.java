package com.example.raceapp.service;

import com.example.raceapp.model.Race;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class RaceService {
    private final List<Race> races = new ArrayList<>();

    public RaceService() {
        races.add(new Race(1, "Grand Prix", 2024));
        races.add(new Race(2, "Le Mans", 2023));
    }

    public List<Race> getAllRaces() {
        return races;
    }

    public Race getRaceById(int id) {
        return races.stream().filter(r -> r.getId() == id).findFirst().orElse(null);
    }
}
