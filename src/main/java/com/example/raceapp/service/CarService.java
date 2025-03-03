package com.example.raceapp.service;

import com.example.raceapp.model.Car;
import com.example.raceapp.repository.CarRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Car entities.
 * Provides business logic for creating, retrieving, updating, and deleting cars.
 */
@Service
@Transactional
public class CarService {
    private final CarRepository carRepository;

    /**
     * Constructs a new CarService with the specified CarRepository.
     *
     * @param carRepository The repository responsible for handling car-related database operations.
     */
    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    /**
     * Creates a new car and saves it to the database.
     *
     * @param car The car object to be created.
     * @return The saved car object.
     */
    public Car createCar(Car car) {
        return carRepository.save(car);
    }

    /**
     * Retrieves a car by its unique identifier.
     * Uses an EntityGraph to eagerly fetch associated entities (owner and races).
     *
     * @param id The unique identifier of the car to retrieve.
     * @return An Optional containing the car if found, or an empty Optional if not found.
     */
    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id); // Использует EntityGraph
    }

    /**
     * Searches for cars based on optional filtering criteria.
     * If no filtering criteria are provided, retrieves all cars.
     *
     * @param brand   (Optional) The brand of the car to filter by.
     * @param model   (Optional) The model of the car to filter by.
     * @param power   (Optional) The power of the car to filter by.
     * @param ownerId (Optional) The ID of the owner (Pilot) to filter by.
     * @return A list of cars matching the criteria, or all cars if no criteria are provided.
     */
    public List<Car> searchCars(String brand, String model, Integer power, Long ownerId) {
        List<Car> cars = new ArrayList<>();

        if (brand != null) {
            cars.addAll(carRepository.findByBrand(brand));
        }
        if (model != null) {
            cars.addAll(carRepository.findByModel(model));
        }
        if (power != null) {
            cars.addAll(carRepository.findByPower(power));
        }
        if (ownerId != null) {
            cars.addAll(carRepository.findByOwnerId(ownerId));
        }

        // Если ни один критерий не был указан, вернуть все автомобили
        if (brand == null && model == null && power == null && ownerId == null) {
            return carRepository.findAll();
        }

        // Удаление дубликатов
        return new ArrayList<>(new HashSet<>(cars));
    }

    /**
     * Updates an existing car by replacing its data with the provided details.
     * Only updates the car if it exists in the database.
     *
     * @param id         The unique identifier of the car to update.
     * @param carDetails The updated car object containing new data.
     * @return An updated car if successful, or an empty Optional if not found.
     */
    public Optional<Car> updateCar(Long id, Car carDetails) {
        return carRepository.findById(id).map(car -> {
            car.setBrand(carDetails.getBrand());
            car.setModel(carDetails.getModel());
            car.setPower(carDetails.getPower());
            return carRepository.save(car);
        });
    }

    /**
     * Partially updates an existing car.
     * Only updates fields that are non-null and valid in the provided car details.
     *
     * @param id         The unique identifier of the car to partially update.
     * @param carDetails The partial car object containing updated fields.
     * @return An updated car if successful, or an empty Optional if not found.
     */
    public Optional<Car> partialUpdateCar(Long id, Car carDetails) {
        return carRepository.findById(id).map(car -> {
            if (carDetails.getBrand() != null && !carDetails.getBrand().isEmpty()) {
                car.setBrand(carDetails.getBrand());
            }
            if (carDetails.getModel() != null && !carDetails.getModel().isEmpty()) {
                car.setModel(carDetails.getModel());
            }
            if (carDetails.getPower() > 0) {
                car.setPower(carDetails.getPower());
            }
            return carRepository.save(car);
        });
    }

    /**
     * Deletes a car by its unique identifier.
     *
     * @param id The unique identifier of the car to delete.
     */
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}