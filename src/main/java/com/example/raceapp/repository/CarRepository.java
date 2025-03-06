package com.example.raceapp.repository;

import com.example.raceapp.model.Car;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


/**
 * Repository for managing cars.
 */
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    /**
     * Retrieves a car by its ID with the associated owner eagerly loaded (via EntityGraph).
     *
     * @param id the ID of the car to find.
     * @return an Optional containing the car if found, or empty otherwise.
     */
    @EntityGraph(attributePaths = {"owner"})
    Optional<Car> findById(Long id);
}