package com.example.raceapp.dao;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;

/**
 * DAO interface for managing Pilot entities in the database.
 * Provides methods to save, find, delete pilots and retrieve races for a pilot.
 */
public interface PilotDao {

    /**
     * Saves the given pilot to the database.
     *
     * @param pilot the pilot to save
     * @return the saved pilot entity
     */
    Pilot save(Pilot pilot);

    /**
     * Finds a pilot by their ID.
     *
     * @param id the ID of the pilot to find
     * @return an Optional containing the found pilot, or an empty Optional if not found
     */
    Optional<Pilot> findById(Long id);

    /**
     * Finds all pilots in the database.
     *
     * @return a list of all pilots
     */
    List<Pilot> findAll();

    /**
     * Deletes a pilot by their ID.
     *
     * @param id the ID of the pilot to delete
     */
    void deleteById(Long id);

    /**
     * Retrieves all races a specific pilot is participating in.
     *
     * @param pilotId the ID of the pilot
     * @return a list of races the pilot is part of
     */
    List<Race> getRacesForPilot(Long pilotId);
}
