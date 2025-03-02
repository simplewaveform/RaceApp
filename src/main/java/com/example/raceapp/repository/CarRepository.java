package com.example.raceapp.repository;

import com.example.raceapp.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Car entities.
 * Provides methods for querying and retrieving cars from the database.
 */
@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    /**
     * Retrieves a list of cars based on optional filtering criteria.
     * The query supports filtering by brand, model, power, and owner ID.
     * If a parameter is null, it is ignored in the filtering process.
     *
     * @param brand   (Optional) The brand of the car to filter by.
     * @param model   (Optional) The model of the car to filter by.
     * @param power   (Optional) The power of the car to filter by.
     * @param ownerId (Optional) The ID of the owner (Pilot) to filter by.
     * @return A list of cars matching the provided filtering criteria.
     */
    @EntityGraph(attributePaths = {"owner", "races"})
    @Query("SELECT c FROM Car c WHERE "
            + "(:brand IS NULL OR c.brand = :brand) AND "
            + "(:model IS NULL OR c.model = :model) AND "
            + "(:power IS NULL OR c.power = :power) AND "
            + "(:ownerId IS NULL OR c.owner.id = :ownerId)")
    List<Car> findByBrandOrModelOrPowerOrOwnerId(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("power") Integer power,
            @Param("ownerId") Long ownerId);

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
}