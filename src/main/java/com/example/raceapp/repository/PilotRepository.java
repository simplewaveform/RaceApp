package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PilotRepository extends JpaRepository<Pilot, Long>,
        JpaSpecificationExecutor<Pilot> {

    @EntityGraph(attributePaths = {"cars"})
    @Override
    List<Pilot> findAll();

    @EntityGraph(attributePaths = {"cars", "races"})
    @Override
    Optional<Pilot> findById(Long id);

    @Query("SELECT p FROM Pilot p JOIN p.cars c WHERE c.brand = :brand")
    Page<Pilot> findPilotsByCarBrand(@Param("brand") String brand, Pageable pageable);

    @Query("SELECT p FROM Pilot p WHERE p.experience > :experience AND SIZE(p.cars) > 0")
    Page<Pilot> findExperiencedPilotsWithCars(@Param("experience") int experience,
                                              Pageable pageable);
}