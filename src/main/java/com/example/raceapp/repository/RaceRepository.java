package com.example.raceapp.repository;

import com.example.raceapp.model.Race;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface RaceRepository extends JpaRepository<Race, Long> {
    @Query("SELECT r FROM Race r WHERE "
            + "(:name IS NULL OR r.name = :name) AND "
            + "(:year IS NULL OR r.year = :year)")
    List<Race> findByNameOrYear(
            @Param("name") String name,
            @Param("year") Integer year);
}