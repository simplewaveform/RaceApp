package com.example.raceapp.repository;

import com.example.raceapp.model.Pilot;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface PilotRepository extends JpaRepository<Pilot, Long> {
    @Query("SELECT p FROM Pilot p WHERE " +
            "(:name IS NULL OR p.name = :name) AND " +
            "(:age IS NULL OR p.age = :age) AND " +
            "(:experience IS NULL OR p.experience = :experience)")
    List<Pilot> findByNameOrAgeOrExperience(
            @Param("name") String name,
            @Param("age") Integer age,
            @Param("experience") Integer experience);
}