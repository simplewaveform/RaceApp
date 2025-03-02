package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Pilot entities.
 * Provides methods for querying and retrieving pilots from the database.
 */
@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {

    /**
     * Retrieves a list of pilots based on optional filtering criteria.
     * The query supports filtering by name, age, and experience.
     * If a parameter is null, it is ignored in the filtering process.
     *
     * @param name       (Optional) The name of the pilot to filter by.
     * @param age        (Optional) The age of the pilot to filter by.
     * @param experience (Optional) The experience of the pilot to filter by.
     * @return A list of pilots matching the provided filtering criteria.
     */
    @EntityGraph(attributePaths = {"cars"})
    @Query("SELECT p FROM Pilot p WHERE "
            + "(:name IS NULL OR p.name = :name) AND "
            + "(:age IS NULL OR p.age = :age) AND "
            + "(:experience IS NULL OR p.experience = :experience)")
    List<Pilot> findByNameOrAgeOrExperience(
            @Param("name") String name,
            @Param("age") Integer age,
            @Param("experience") Integer experience);

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
}