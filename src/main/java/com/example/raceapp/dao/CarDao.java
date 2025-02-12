package com.example.raceapp.dao;

import com.example.raceapp.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Data Access Object (DAO) interface for managing {@link Car} entities.
 */
@Repository
public interface CarDao {
    /**
     * Saves a car entity.
     *
     * @param car the {@link Car} object to save
     * @return the saved {@link Car} object
     */
    Car save(Car car);

    /**
     * Finds a car by its ID.
     *
     * @param id the car ID
     * @return an {@link Optional} containing the found {@link Car}, or empty if not found
     */
    Optional<Car> findById(Long id);

    /**
     * Retrieves all cars.
     *
     * @return a list of all {@link Car} entities
     */
    List<Car> findAll();

    /**
     * Deletes a car by its ID.
     *
     * @param id the car ID
     */
    void deleteById(Long id);

    /**
     * Retrieves cars by their brand.
     *
     * @param brand the brand of the cars to filter by
     * @return a list of {@link Car} entities with the specified brand
     */
    List<Car> findByBrand(String brand);
}