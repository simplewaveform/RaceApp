package com.example.raceapp.service;

import com.example.raceapp.dao.PilotDao;
import com.example.raceapp.dao.RaceDao;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to races.
 * Interacts with the RaceDao for CRUD operations and other race-related functionality.
 */
@Service
public class RaceService {
    private final RaceDao raceDao;
    private final PilotDao pilotDao;

    /**
     * Constructor to initialize the RaceService with a RaceDao and PilotDao dependency.
     *
     * @param raceDao the RaceDao instance for performing database operations.
     * @param pilotDao the PilotDao instance for performing database operations.
     */
    @Autowired
    public RaceService(RaceDao raceDao, PilotDao pilotDao) {
        this.raceDao = raceDao;
        this.pilotDao = pilotDao;
    }

    /**
     * Saves a race entity to the database.
     *
     * @param race the race entity to save.
     * @return the saved race entity.
     */
    public Race saveRace(Race race) {
        return raceDao.save(race);
    }

    /**
     * Retrieves all races from the database.
     *
     * @return a list of all races.
     */
    public List<Race> getAllRaces() {
        return raceDao.findAll();
    }

    /**
     * Retrieves a race by its ID.
     *
     * @param id the ID of the race to retrieve.
     * @return the race if found, or null if not found.
     */
    public Race getRaceById(Long id) {
        Optional<Race> race = raceDao.findById(id);
        return race.orElse(null);
    }

    /**
     * Deletes a race by its ID.
     *
     * @param id the ID of the race to delete.
     */
    public void deleteRace(Long id) {
        raceDao.deleteById(id);
    }

    /**
     * Retrieves all pilots participating in a specific race.
     *
     * @param id the ID of the race for which to retrieve the pilots.
     * @return a list of pilots participating in the specified race.
     */
    public List<Pilot> getPilotsForRace(Long id) {
        return raceDao.getPilotsForRace(id);
    }

    /**
     * Adds a pilot to a race.
     *
     * @param raceId the ID of the race.
     * @param pilotId the ID of the pilot to add.
     * @return the updated race entity.
     */
    public Race addPilotToRace(Long raceId, Long pilotId) {
        Race race = raceDao.findById(raceId).orElse(null);
        Pilot pilot = pilotDao.findById(pilotId).orElse(null);

        if (race != null && pilot != null) {
            race.getPilots().add(pilot);
            pilot.getRaces().add(race);
            return raceDao.save(race);
        }

        return null;
    }

    /**
     * Retrieves a pilot by their ID.
     *
     * @param id the ID of the pilot to retrieve.
     * @return the pilot if found, or null if not found.
     */
    public Pilot getPilotById(Long id) {
        Optional<Pilot> pilot = pilotDao.findById(id);
        return pilot.orElse(null);
    }
}