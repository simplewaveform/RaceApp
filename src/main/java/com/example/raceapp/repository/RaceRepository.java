package com.example.raceapp.repository;

import com.example.raceapp.model.Race;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository for managing races.
 */
public interface RaceRepository extends JpaRepository<Race, Long> {

    /**
     * Retrieves a race by its ID with associated pilots and cars eagerly loaded (via EntityGraph).
     *
     * @param id the ID of the race to find.
     * @return an Optional containing the race if found, or empty otherwise.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    Optional<Race> findById(Long id);
}