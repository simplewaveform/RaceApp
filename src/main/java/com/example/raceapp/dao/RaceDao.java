package com.example.raceapp.dao;

import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * DAO interface for Race entity.
 * Defines database operations for Race.
 */
@Repository
public interface RaceDao {
    /**
     * Saves a Race entity.
     *
     * @param race the Race to save.
     * @return the saved Race entity.
     */
    Race save(Race race);

    /**
     * Finds a Race by its ID.
     *
     * @param id the Race ID.
     * @return an Optional containing the Race, or empty if not found.
     */
    Optional<Race> findById(Long id);

    /**
     * Retrieves all Races.
     *
     * @return a list of all Races.
     */
    List<Race> findAll();

    /**
     * Deletes a Race by its ID.
     *
     * @param id the Race ID.
     */
    void deleteById(Long id);
}
