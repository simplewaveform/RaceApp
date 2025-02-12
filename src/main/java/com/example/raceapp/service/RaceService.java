package com.example.raceapp.service;

import com.example.raceapp.dao.RaceDao;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RaceService {
    private final RaceDao raceDao;

    public RaceService(RaceDao raceDao) {
        this.raceDao = raceDao;
    }

    public Race saveRace(Race race) { // <- Метод сохранения гонки
        return raceDao.save(race);
    }

    public List<Race> getAllRaces() {
        return raceDao.findAll();
    }

    public Race getRaceById(Long id) {
        Optional<Race> race = raceDao.findById(id);
        return race.orElse(null);
    }

    public void deleteRace(Long id) {
        raceDao.deleteById(id);
    }

    public List<Pilot> getPilotsForRace(Long id) { // <- Метод получения пилотов для гонки
        return raceDao.getPilotsForRace(id);
    }
}
