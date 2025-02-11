package com.example.raceapp.service;

import com.example.raceapp.model.Race;
import com.example.raceapp.repository.RaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RaceService {
    private final RaceRepository raceRepository;

    @Autowired
    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    public Race getRaceById(Long id) {
        return raceRepository.findById(id).orElse(null);
    }

    public List<Race> getAllRaces() {
        return raceRepository.findAll();
    }

    public Race saveRace(Race race) {
        return raceRepository.save(race);
    }
}
