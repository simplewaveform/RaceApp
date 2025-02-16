package com.example.raceapp.dao;

import com.example.raceapp.model.Pilot;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * DAO interface for Pilot entity.
 * Defines database operations for Pilot.
 */
@Repository
public interface PilotDao {
    /**
     * Saves a Pilot entity.
     *
     * @param pilot the Pilot to save.
     * @return the saved Pilot entity.
     */
    Pilot save(Pilot pilot);

    /**
     * Finds a Pilot by its ID.
     *
     * @param id the Pilot ID.
     * @return an Optional containing the Pilot, or empty if not found.
     */
    Optional<Pilot> findById(Long id);

    /**
     * Retrieves all Pilots.
     *
     * @return a list of all Pilots.
     */
    List<Pilot> findAll();

    /**
     * Deletes a Pilot by its ID.
     *
     * @param id the Pilot ID.
     */
    void deleteById(Long id);
}
