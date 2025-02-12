package com.example.raceapp.dao;

import com.example.raceapp.model.Car;
import java.util.List;
import java.util.Optional;

public interface CarDao {
    Car save(Car car);
    Optional<Car> findById(Long id);
    List<Car> findAll();
    void deleteById(Long id);
}
