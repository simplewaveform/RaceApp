package com.example.raceapp.dao;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;

public interface PilotDao {
    Pilot save(Pilot pilot);
    Optional<Pilot> findById(Long id);
    List<Pilot> findAll();
    void deleteById(Long id);
    List<Race> getRacesForPilot(Long pilotId);
}
