package com.example.raceapp.repository;

import com.example.raceapp.model.Car;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

/**
 * Repository for managing cars.
 */
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    @EntityGraph(attributePaths = {"owner"})
    Optional<Car> findById(Long id);
}