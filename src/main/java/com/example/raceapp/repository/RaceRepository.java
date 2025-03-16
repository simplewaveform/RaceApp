package com.example.raceapp.repository;

import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository for managing races with explicit EntityGraph configurations to avoid N+1 queries.
 */
public interface RaceRepository extends JpaRepository<Race, Long> {

    /**
     * Retrieves all races with their associated pilots and cars loaded eagerly.
     *
     * @return a list of all races with pilots and cars.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    @Override
    List<Race> findAll();

    /**
     * Retrieves a race by its ID with associated pilots and cars eagerly loaded.
     *
     * @param id the ID of the race to retrieve.
     * @return an Optional containing the race if found, or empty otherwise.
     */
    @EntityGraph(attributePaths = {"pilots", "cars"})
    @Override
    Optional<Race> findById(Long id);

    @Query(value = "SELECT * FROM races WHERE year BETWEEN :start AND :end", nativeQuery = true)
    Page<Race> findRacesByYearRange(@Param("start") int start, @Param("end") int end,
                                    Pageable pageable);
}