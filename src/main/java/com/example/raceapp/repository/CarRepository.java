package com.example.raceapp.repository;

import com.example.raceapp.model.Car;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {
    @Query("SELECT c FROM Car c WHERE "
            + "(:brand IS NULL OR c.brand = :brand) AND "
            + "(:model IS NULL OR c.model = :model) AND "
            + "(:power IS NULL OR c.power = :power) AND "
            + "(:ownerId IS NULL OR c.owner.id = :ownerId)")
    List<Car> findByBrandOrModelOrPowerOrOwnerId(
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("power") Integer power,
            @Param("ownerId") Long ownerId);
}