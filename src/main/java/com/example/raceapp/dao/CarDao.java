package com.example.raceapp.dao;

import com.example.raceapp.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * DAO interface for Car entity.
 * Defines database operations for Car.
 */
@Repository
public interface CarDao {
    /**
     * Saves a Car entity.
     *
     * @param car the Car to save.
     * @return the saved Car entity.
     */
    Car save(Car car);

    /**
     * Finds a Car by its ID.
     *
     * @param id the Car ID.
     * @return an Optional containing the Car, or empty if not found.
     */
    Optional<Car> findById(Long id);

    /**
     * Retrieves all Cars.
     *
     * @return a list of all Cars.
     */
    List<Car> findAll();

    /**
     * Deletes a Car by its ID.
     *
     * @param id the Car ID.
     */
    void deleteById(Long id);
}
