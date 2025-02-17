package com.example.raceapp.service;

import com.example.raceapp.dao.CarDao;
import com.example.raceapp.model.Car;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing Car entities.
 * Provides business logic for Car CRUD operations.
 */
@Service
public class CarService {
    private final CarDao carDao;

    /**
     * Constructor for the CarService class.
     *
     * @param carDao The CarDao instance to be injected into this service.
     */
    @Autowired
    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    /**
     * Creates a new Car entity.
     *
     * @param car the Car to create.
     * @return the saved Car entity.
     */
    public Car createCar(Car car) {
        return carDao.save(car);
    }

    /**
     * Retrieves a Car by its ID.
     *
     * @param id the ID of the Car.
     * @return an Optional containing the Car if found, or empty otherwise.
     */
    public Optional<Car> getCarById(Long id) {
        return carDao.findById(id);
    }

    /**
     * Retrieves all Cars.
     *
     * @return a list of all Cars.
     */
    public List<Car> getAllCars() {
        return carDao.findAll();
    }

    public List<Car> getCarsByBrand(String brand) {
        return carDao.findByBrand(brand);
    }

    /**
     * Fully updates a Car entity.
     *
     * @param id the ID of the Car.
     * @param carDetails the new Car data.
     * @return an Optional containing the updated Car if found, or empty otherwise.
     */
    public Optional<Car> updateCar(Long id, Car carDetails) {
        return carDao.findById(id).map(car -> {
            car.setBrand(carDetails.getBrand());
            car.setModel(carDetails.getModel());
            car.setPower(carDetails.getPower());
            return carDao.save(car);
        });
    }

    /**
     * Partially updates a Car entity.
     *
     * @param id the ID of the Car.
     * @param carDetails the partial Car data.
     * @return an Optional containing the updated Car if found, or empty otherwise.
     */
    public Optional<Car> partialUpdateCar(Long id, Car carDetails) {
        return carDao.findById(id).map(car -> {
            if (carDetails.getBrand() != null && !carDetails.getBrand().isEmpty()) {
                car.setBrand(carDetails.getBrand());
            }
            if (carDetails.getModel() != null && !carDetails.getModel().isEmpty()) {
                car.setModel(carDetails.getModel());
            }
            if (carDetails.getPower() > 0) { // Проверка на положительное значение
                car.setPower(carDetails.getPower());
            }
            return carDao.save(car);
        });
    }

    /**
     * Deletes a Car by its ID.
     *
     * @param id the ID of the Car to delete.
     */
    public void deleteCar(Long id) {
        carDao.deleteById(id);
    }
}
