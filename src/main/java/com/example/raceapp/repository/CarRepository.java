package com.example.raceapp.repository;

import com.example.raceapp.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for managing cars with explicit EntityGraph configurations to load owner associations.
 */
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    /**
     * Retrieves all cars with their owner association loaded eagerly.
     *
     * @return a list of all cars with owners.
     */
    @EntityGraph(attributePaths = {"owner"})
    @Override
    List<Car> findAll();

    /**
     * Retrieves cars matching the given specification with the owner association loaded eagerly.
     *
     * @param spec the specification to filter cars.
     * @return a list of filtered cars with owners.
     */
    @EntityGraph(attributePaths = {"owner"})
    @Override
    List<Car> findAll(Specification<Car> spec);

    /**
     * Retrieves a car by its ID with the owner association loaded eagerly.
     *
     * @param id the ID of the car to retrieve.
     * @return an Optional containing the car if found, or empty otherwise.
     */
    @EntityGraph(attributePaths = {"owner"})
    @Override
    Optional<Car> findById(Long id);

    @EntityGraph(attributePaths = {"owner"})
    @Query("SELECT c FROM Car c WHERE c.owner.id = :ownerId")
    Page<Car> findByOwner(@Param("ownerId") Long ownerId, Pageable pageable);

    @Query(value = "SELECT * FROM cars WHERE power > :minPower", nativeQuery = true)
    Page<Car> findCarsByPowerNative(@Param("minPower") Integer power, Pageable pageable);


}