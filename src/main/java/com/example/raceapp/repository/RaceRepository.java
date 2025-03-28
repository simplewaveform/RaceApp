package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing {@link Race} entities.
 * Provides methods to perform CRUD operations and custom queries
 * with optimized data fetching using EntityGraph.
 */
public interface RaceRepository extends JpaRepository<Race, Long> {

    /**
     * Retrieves all races with their associated pilots and cars eagerly loaded.
     * This method prevents N+1 query issues by using EntityGraph.
     *
     * @return A list of all races with detailed pilot and car information.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    @Override
    List<Race> findAll();

    /**
     * Retrieves a race by its ID with associated pilots and cars eagerly loaded.
     * This method prevents N+1 query issues by using EntityGraph.
     *
     * @param id The ID of the race to retrieve.
     * @return An {@link Optional} containing the race if found, or empty otherwise.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    @Override
    Optional<Race> findById(Long id);

    /**
     * Retrieves races that occurred within the specified year range.
     * Utilizes a native SQL query for optimized data retrieval.
     *
     * @param start The starting year (inclusive).
     * @param end The ending year (inclusive).
     * @param pageable Pagination details for efficient data handling.
     * @return A paginated list of races that fall within the specified year range.
     */
    @Query(value = "SELECT * FROM races WHERE year BETWEEN :start AND :end", nativeQuery = true)
    Page<Race> findRacesByYearRangeNative(@Param("start") Integer start,
                                    @Param("end") Integer end,
                                    Pageable pageable);

    List<Race> findByPilotsContaining(Pilot pilot);

    /**
     * Retrieves races that occurred within the specified year range.
     * Uses JPQL instead of native SQL.
     *
     * @param start The starting year (inclusive).
     * @param end The ending year (inclusive).
     * @param pageable Pagination details for efficient data handling.
     * @return A paginated list of races that fall within the specified year range.
     */
    @Query("SELECT r FROM Race r WHERE r.year BETWEEN :start AND :end")
    Page<Race> findRacesByYearRange(@Param("start") Integer start,
                                    @Param("end") Integer end,
                                    Pageable pageable);

}
