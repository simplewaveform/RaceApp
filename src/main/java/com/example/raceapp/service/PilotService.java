package com.example.raceapp.service;

import com.example.raceapp.dao.PilotDao;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PilotService {
    private final PilotDao pilotDao;

    public PilotService(PilotDao pilotDao) {
        this.pilotDao = pilotDao;
    }

    public Pilot createPilot(Pilot pilot) {
        return pilotDao.save(pilot);
    }

    public List<Pilot> getAllPilots() {
        return pilotDao.findAll();
    }

    public Pilot getPilotById(Long id) {
        Optional<Pilot> pilot = pilotDao.findById(id);
        return pilot.orElse(null);
    }

    public void deletePilot(Long id) {
        pilotDao.deleteById(id);
    }

    public List<Race> getRacesForPilot(Long id) {
        return pilotDao.getRacesForPilot(id);
    }
}
