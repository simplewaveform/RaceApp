package com.example.raceapp.dao;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object (DAO) interface for performing CRUD operations on Race entities.
 * Provides methods for saving, retrieving, and deleting races, as well as fetching
 * pilots for a specific race.
 */
@Repository
public interface RaceDao {
    /**
     * Saves a given race to the database.
     *
     * @param race the race to be saved.
     * @return the saved race entity.
     */
    Race save(Race race);

    /**
     * Finds a race by its ID.
     *
     * @param id the ID of the race to retrieve.
     * @return an Optional containing the race if found, or empty if not found.
     */
    Optional<Race> findById(Long id);

    /**
     * Retrieves all races from the database.
     *
     * @return a list of all races.
     */
    List<Race> findAll();

    /**
     * Deletes a race by its ID.
     *
     * @param id the ID of the race to be deleted.
     */
    void deleteById(Long id);

    /**
     * Retrieves all pilots participating in a specific race.
     *
     * @param raceId the ID of the race to get pilots for.
     * @return a list of pilots participating in the specified race.
     */
    List<Pilot> getPilotsForRace(Long raceId);
}