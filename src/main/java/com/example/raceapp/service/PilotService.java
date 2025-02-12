package com.example.raceapp.service;

import com.example.raceapp.dao.PilotDao;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Service class for managing Pilot entities.
 * Provides methods for creating, retrieving, deleting pilots, and fetching races for a pilot.
 */
@Service
public class PilotService {
    private final PilotDao pilotDao;

    /**
     * Constructs a PilotService with the specified PilotDao.
     *
     * @param pilotDao the PilotDao instance to interact with the data layer
     */
    public PilotService(PilotDao pilotDao) {
        this.pilotDao = pilotDao;
    }

    /**
     * Creates or updates a pilot.
     *
     * @param pilot the pilot to be created or updated
     * @return the saved or updated pilot entity
     */
    public Pilot createPilot(Pilot pilot) {
        return pilotDao.save(pilot);
    }

    /**
     * Retrieves all pilots.
     *
     * @return a list of all pilots
     */
    public List<Pilot> getAllPilots() {
        return pilotDao.findAll();
    }

    /**
     * Retrieves a pilot by their ID.
     *
     * @param id the ID of the pilot to retrieve
     * @return the pilot with the given ID, or null if not found
     */
    public Pilot getPilotById(Long id) {
        Optional<Pilot> pilot = pilotDao.findById(id);
        return pilot.orElse(null);
    }

    /**
     * Deletes a pilot by their ID.
     *
     * @param id the ID of the pilot to delete
     */
    public void deletePilot(Long id) {
        pilotDao.deleteById(id);
    }

    /**
     * Retrieves all races a specific pilot is participating in.
     *
     * @param id the ID of the pilot
     * @return a list of races the pilot is part of
     */
    public List<Race> getRacesForPilot(Long id) {
        return pilotDao.getRacesForPilot(id);
    }
}
