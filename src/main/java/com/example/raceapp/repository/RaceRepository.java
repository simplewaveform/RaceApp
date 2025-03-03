package com.example.raceapp.repository;

import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Race entities.
 * Provides methods for querying and retrieving races from the database.
 */
@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {

    /**
     * Retrieves a race by its unique identifier, including its associated pilots and cars.
     * Uses an entity graph to eagerly fetch the related entities (pilots and cars).
     *
     * @param id The unique identifier of the race to retrieve.
     * @return An Optional containing the race if found, or an empty Optional if not found.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    @Override
    Optional<Race> findById(Long id);

    /**
     * Retrieves a list of races based on the name.
     * Uses an entity graph to eagerly fetch the related entities (pilots and cars).
     *
     * @param name The name of the race to filter by.
     * @return A list of races matching the provided name.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    List<Race> findByName(String name);

    /**
     * Retrieves a list of races based on the year.
     * Uses an entity graph to eagerly fetch the related entities (pilots and cars).
     *
     * @param year The year of the race to filter by.
     * @return A list of races matching the provided year.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    List<Race> findByYear(Integer year);
}