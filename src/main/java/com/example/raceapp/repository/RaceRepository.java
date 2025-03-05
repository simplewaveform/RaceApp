package com.example.raceapp.repository;

import com.example.raceapp.model.Race;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repository for managing races.
 */
public interface RaceRepository extends JpaRepository<Race, Long> {

    @EntityGraph(attributePaths = {"pilots", "cars"})
    Optional<Race> findById(Long id);
}