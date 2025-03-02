package com.example.raceapp.repository;

import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Race entities.
 * Provides methods for querying and retrieving races from the database.
 */
@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {

    /**
     * Retrieves a list of races based on optional filtering criteria.
     * The query supports filtering by name and year.
     * If a parameter is null, it is ignored in the filtering process.
     *
     * @param name (Optional) The name of the race to filter by.
     * @param year (Optional) The year of the race to filter by.
     * @return A list of races matching the provided filtering criteria.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    @Query("SELECT r FROM Race r WHERE "
            + "(:name IS NULL OR r.name = :name) AND "
            + "(:year IS NULL OR r.year = :year)")
    List<Race> findByNameOrYear(
            @Param("name") String name,
            @Param("year") Integer year);

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
}