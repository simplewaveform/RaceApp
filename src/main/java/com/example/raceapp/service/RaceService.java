package com.example.raceapp.service;

import com.example.raceapp.model.Race;
import com.example.raceapp.repository.RaceRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class RaceService {
    private final RaceRepository raceRepository;

    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    public Race createRace(Race race) {
        return raceRepository.save(race);
    }

    public Optional<Race> getRaceById(Long id) {
        return raceRepository.findById(id); // Использует EntityGraph
    }

    public List<Race> searchRaces(String name, Integer year) {
        if (name != null || year != null) {
            return raceRepository.findByNameOrYear(name, year);
        }
        return raceRepository.findAll();
    }

    public Optional<Race> updateRace(Long id, Race raceDetails) {
        return raceRepository.findById(id).map(race -> {
            race.setName(raceDetails.getName());
            race.setYear(raceDetails.getYear());
            return raceRepository.save(race);
        });
    }

    public Optional<Race> partialUpdateRace(Long id, Race raceDetails) {
        return raceRepository.findById(id).map(race -> {
            if (raceDetails.getName() != null) {
                race.setName(raceDetails.getName());
            }
            if (raceDetails.getYear() != null) {
                race.setYear(raceDetails.getYear());
            }
            return raceRepository.save(race);
        });
    }

    public void deleteRace(Long id) {
        raceRepository.deleteById(id);
    }
}