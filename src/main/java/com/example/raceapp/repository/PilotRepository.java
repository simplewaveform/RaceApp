package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Pilot entities.
 * Provides methods for querying and retrieving pilots from the database.
 */
@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {

    /**
     * Retrieves a pilot by its unique identifier, including its associated cars.
     * Uses an entity graph to eagerly fetch the related cars.
     *
     * @param id The unique identifier of the pilot to retrieve.
     * @return An Optional containing the pilot if found, or an empty Optional if not found.
     */
    @EntityGraph(attributePaths = {"cars"})
    @Override
    Optional<Pilot> findById(Long id);

    /**
     * Retrieves a list of pilots based on the name.
     * Uses an entity graph to eagerly fetch the related cars.
     *
     * @param name The name of the pilot to filter by.
     * @return A list of pilots matching the provided name.
     */
    @EntityGraph(attributePaths = {"cars"})
    List<Pilot> findByName(String name);

    /**
     * Retrieves a list of pilots based on the age.
     * Uses an entity graph to eagerly fetch the related cars.
     *
     * @param age The age of the pilot to filter by.
     * @return A list of pilots matching the provided age.
     */
    @EntityGraph(attributePaths = {"cars"})
    List<Pilot> findByAge(Integer age);

    /**
     * Retrieves a list of pilots based on the experience.
     * Uses an entity graph to eagerly fetch the related cars.
     *
     * @param experience The experience of the pilot to filter by.
     * @return A list of pilots matching the provided experience.
     */
    @EntityGraph(attributePaths = {"cars"})
    List<Pilot> findByExperience(Integer experience);
}