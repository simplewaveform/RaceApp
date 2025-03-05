package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing pilots.
 */
public interface PilotRepository extends
        JpaRepository<Pilot, Long>,
        JpaSpecificationExecutor<Pilot> { // Добавлено!

    @EntityGraph(attributePaths = {"cars", "races"})
    Optional<Pilot> findById(Long id);

    @EntityGraph(attributePaths = {"cars"})
    @Override
    List<Pilot> findAll();
}