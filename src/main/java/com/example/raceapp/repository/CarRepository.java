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
 * Repository interface for managing {@link Car} entities.
 * Provides methods to query and manipulate car data,
 * including eager loading of associated owner entities.
 */
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    /**
     * Retrieves all {@link Car} entities with their associated owner eagerly loaded.
     * This method overrides the default {@link JpaRepository#findAll()} method
     * to ensure that the "owner" association is eagerly fetched.
     *
     * @return a {@link List} of all {@link Car} entities with their associated owner.
     */
    @EntityGraph(attributePaths = {"owner"})
    @Override
    List<Car> findAll();

    /**
     * Retrieves all {@link Car} entities that match the given {@link Specification}
     * with their associated owner eagerly loaded.
     * This method overrides the default method to ensure the "owner" association
     * is eagerly fetched for all matching cars.
     *
     * @param spec the {@link Specification} to filter the cars by.
     * @return a {@link List} of {@link Car} entities that match the
     *         given specification, with their associated owner eagerly loaded.
     */
    @EntityGraph(attributePaths = {"owner"})
    @Override
    List<Car> findAll(Specification<Car> spec);

    /**
     * Retrieves a {@link Car} entity by its ID with the associated owner eagerly loaded.
     * This method overrides the default  method to ensure that the "owner" association
     * is eagerly loaded for the retrieved car.
     *
     * @param id the ID of the {@link Car} to retrieve.
     * @return an {@link Optional} containing the {@link Car} if found,
     *         or {@link Optional#empty()} if not found.
     */
    @EntityGraph(attributePaths = {"owner"})
    @Override
    Optional<Car> findById(Long id);

    /**
     * Retrieves a paginated list of {@link Car} entities
     * where the owner ID matches the provided {@code ownerId}.
     * The "owner" association is eagerly loaded for each car in the result.
     * This method uses a custom JPQL query to filter the cars based on the owner's ID.
     *
     * @param ownerId the ID of the owner whose cars should be retrieved.
     * @param pageable a {@link Pageable} object to apply pagination to the result.
     * @return a {@link Page} of {@link Car} entities that belong to the
     *         specified owner, with their associated owner eagerly loaded.
     */
    @EntityGraph(attributePaths = {"owner"})
    @Query("SELECT c FROM Car c WHERE c.owner.id = :ownerId")
    Page<Car> findByOwner(@Param("ownerId") Long ownerId, Pageable pageable);

    /**
     * Retrieves a paginated list of {@link Car} entities
     * with a power greater than the specified {@code minPower}.
     * This method uses a native SQL query to filter the cars based on their power.
     *
     * @param power the minimum power threshold for the cars to be retrieved.
     * @param pageable a {@link Pageable} object to apply pagination to the result.
     * @return a {@link Page} of {@link Car} entities that
     *         have power greater than the specified minimum power.
     */
    @Query(value = "SELECT * FROM cars WHERE power > :minPower", nativeQuery = true)
    Page<Car> findCarsByPowerNative(@Param("minPower") Integer power, Pageable pageable);

}
