package com.example.raceapp.service;

import com.example.raceapp.dao.CarDao;
import com.example.raceapp.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service class for managing car operations.
 */
@Service
public class CarService {
    private final CarDao carDao;

    /**
     * Constructs a {@link CarService} with the given {@link CarDao}.
     *
     * @param carDao the data access object for {@link Car}
     */
    @Autowired
    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    /**
     * Creates and saves a new car.
     *
     * @param car the {@link Car} to be saved
     * @return the saved {@link Car} entity
     */
    public Car createCar(Car car) {
        return carDao.save(car);
    }

    /**
     * Retrieves all cars.
     *
     * @return a list of all {@link Car} entities
     */
    public List<Car> getAllCars() {
        return carDao.findAll();
    }

    /**
     * Finds a car by its ID.
     *
     * @param id the ID of the car
     * @return the found {@link Car} entity, or {@code null} if not found
     */
    public Car getCarById(Long id) {
        Optional<Car> car = carDao.findById(id);
        return car.orElse(null);
    }

    /**
     * Deletes a car by its ID.
     *
     * @param id the ID of the car to delete
     */
    public void deleteCar(Long id) {
        carDao.deleteById(id);
    }

    /**
     * Retrieves cars by their brand.
     *
     * @param brand the brand of the cars to filter by
     * @return a list of {@link Car} entities with the specified brand
     */
    public List<Car> getCarsByBrand(String brand) {
        return carDao.findByBrand(brand);
    }
}