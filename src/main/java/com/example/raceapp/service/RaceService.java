package com.example.raceapp.service;

import com.example.raceapp.dao.RaceDao;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

/**
 * Service class that handles business logic related to races.
 * Interacts with the RaceDao for CRUD operations and other race-related functionality.
 */
@Service
public class RaceService {
    private final RaceDao raceDao;

    /**
     * Constructor to initialize the RaceService with a RaceDao dependency.
     *
     * @param raceDao the RaceDao instance for performing database operations.
     */
    public RaceService(RaceDao raceDao) {
        this.raceDao = raceDao;
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
}
