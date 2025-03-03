package com.example.raceapp.repository;

import com.example.raceapp.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Car entities.
 * Provides methods for querying and retrieving cars from the database.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    /**
     * Retrieves a car by its unique identifier, including its associated owner and races.
     * Uses an entity graph to eagerly fetch the related entities (owner and races).
     *
     * @param id The unique identifier of the car to retrieve.
     * @return An Optional containing the car if found, or an empty Optional if not found.
     */
    @EntityGraph(attributePaths = {"owner", "races"})
    @Override
    Optional<Car> findById(Long id);

    /**
     * Retrieves a list of cars based on the brand.
     * Uses an entity graph to eagerly fetch the related entities (owner and races).
     *
     * @param brand The brand of the car to filter by.
     * @return A list of cars matching the provided brand.
     */
    @EntityGraph(attributePaths = {"owner", "races"})
    List<Car> findByBrand(String brand);

    /**
     * Retrieves a list of cars based on the model.
     * Uses an entity graph to eagerly fetch the related entities (owner and races).
     *
     * @param model The model of the car to filter by.
     * @return A list of cars matching the provided model.
     */
    @EntityGraph(attributePaths = {"owner", "races"})
    List<Car> findByModel(String model);

    /**
     * Retrieves a list of cars based on the power.
     * Uses an entity graph to eagerly fetch the related entities (owner and races).
     *
     * @param power The power of the car to filter by.
     * @return A list of cars matching the provided power.
     */
    @EntityGraph(attributePaths = {"owner", "races"})
    List<Car> findByPower(Integer power);

    /**
     * Retrieves a list of cars based on the owner ID.
     * Uses an entity graph to eagerly fetch the related entities (owner and races).
     *
     * @param ownerId The ID of the owner (Pilot) to filter by.
     * @return A list of cars matching the provided owner ID.
     */
    @EntityGraph(attributePaths = {"owner", "races"})
    List<Car> findByOwnerId(Long ownerId);
}