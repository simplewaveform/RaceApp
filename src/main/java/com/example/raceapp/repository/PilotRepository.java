package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository for managing pilots.
 */
public interface PilotRepository extends
        JpaRepository<Pilot, Long>,
        JpaSpecificationExecutor<Pilot> { // Добавлено!

    /**
     * Retrieves a pilot by their ID with associated cars and races eagerly loaded via EntityGraph.
     *
     * @param id the ID of the pilot to retrieve
     * @return an {@link Optional} containing the pilot with loaded associations if found,
     *         or empty if no pilot exists with the given ID
     */
    @EntityGraph(attributePaths = {"cars", "races"})
    Optional<Pilot> findById(Long id);

    /**
     * Retrieves all pilots with their associated cars eagerly loaded (via EntityGraph).
     *
     * @return a list of all pilots.
     */
    @EntityGraph(attributePaths = {"cars"})
    @Override
    List<Pilot> findAll();
}