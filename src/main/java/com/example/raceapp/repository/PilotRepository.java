package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * Repository for managing pilots with explicit EntityGraph configurations to load associations.
 */
public interface PilotRepository extends JpaRepository<Pilot, Long>,
        JpaSpecificationExecutor<Pilot> {

    /**
     * Retrieves all pilots with their associated cars loaded eagerly.
     *
     * @return a list of all pilots with cars.
     */
    @EntityGraph(attributePaths = {"cars"})
    @Override
    List<Pilot> findAll();

    /**
     * Retrieves a pilot by their ID with associated cars and races loaded eagerly.
     *
     * @param id the ID of the pilot to retrieve.
     * @return an Optional containing the pilot if found, or empty otherwise.
     */
    @EntityGraph(attributePaths = {"cars", "races"})
    @Override
    Optional<Pilot> findById(Long id);
}