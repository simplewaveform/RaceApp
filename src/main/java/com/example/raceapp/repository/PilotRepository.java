package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing {@link Pilot} entities.
 * Provides methods to perform CRUD operations and custom queries.
 */
public interface PilotRepository extends JpaRepository<Pilot, Long>,
        JpaSpecificationExecutor<Pilot> {

    /**
     * Retrieves all pilots with their associated cars eagerly loaded.
     *
     * @return List of all pilots with car information.
     */
    @EntityGraph(attributePaths = {"cars"})
    @Override
    List<Pilot> findAll();

    /**
     * Retrieves a pilot by its ID with associated cars and races eagerly loaded.
     *
     * @param id The ID of the pilot.
     * @return An {@link Optional} containing the pilot if found, or empty if not.
     */
    @EntityGraph(attributePaths = {"cars", "races"})
    @Override
    Optional<Pilot> findById(Long id);

    /**
     * Finds pilots who own a car of a specified brand.
     *
     * @param brand The brand of the car.
     * @param pageable Pagination information.
     * @return A paginated list of pilots with cars of the specified brand.
     */
    @Query("SELECT p FROM Pilot p JOIN p.cars c WHERE c.brand = :brand")
    Page<Pilot> findPilotsByCarBrand(@Param("brand") String brand, Pageable pageable);

    /**
     * Finds pilots who have more than the specified years of experience
     * and possess at least one car.
     *
     * @param experience Minimum required years of experience.
     * @param pageable Pagination information.
     * @return A paginated list of experienced pilots with cars.
     */
    @Query("SELECT p FROM Pilot p WHERE p.experience > :experience AND SIZE(p.cars) > 0")
    Page<Pilot> findExperiencedPilotsWithCars(@Param("experience") int experience,
                                              Pageable pageable);
}
