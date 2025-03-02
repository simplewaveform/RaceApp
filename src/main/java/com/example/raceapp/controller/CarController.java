package com.example.raceapp.controller;

import com.example.raceapp.model.Car;
import com.example.raceapp.service.CarService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for managing Car entities.
 * Provides endpoints for creating, retrieving, updating, and deleting cars.
 */
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    /**
     * Constructs a new CarController with the specified CarService.
     *
     * @param carService The service responsible for handling car-related operations.
     */
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Creates a new car.
     * Accepts a JSON representation of a car and delegates the creation process to the CarService.
     *
     * @param car The car object to be created, provided in the request body.
     * @return A ResponseEntity containing the created car and an HTTP status code of 201 (CREATED).
     */
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(car));
    }

    /**
     * Retrieves a car by its unique identifier.
     *
     * @param id The unique identifier of the car to retrieve.
     * @return A ResponseEntity containing the car if found, or an empty response if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.of(carService.getCarById(id));
    }

    /**
     * Searches for cars based on optional query parameters.
     * Supports filtering by brand, model, power, and owner ID.
     *
     * @param brand   (Optional) The brand of the car to filter by.
     * @param model   (Optional) The model of the car to filter by.
     * @param power   (Optional) The power of the car to filter by.
     * @param ownerId (Optional) The ID of the owner (Pilot) to filter by.
     * @return A ResponseEntity containing a list of cars matching the search criteria.
     */
    @GetMapping
    public ResponseEntity<List<Car>> getCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer power,
            @RequestParam(required = false) Long ownerId) {
        List<Car> cars = carService.searchCars(brand, model, power, ownerId);
        return ResponseEntity.ok(cars);
    }

    /**
     * Updates an existing car by replacing its data with the provided car object.
     * The car ID in the path must match the ID of the car being updated.
     *
     * @param id  The unique identifier of the car to update.
     * @param car The updated car object, provided in the request body.
     * @return an updated car is successful, or an empty response if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        return ResponseEntity.of(carService.updateCar(id, car));
    }

    /**
     * Partially updates an existing car.
     * Only the fields present in the provided car object will be updated.
     *
     * @param id  The unique identifier of the car to partially update.
     * @param car The partial car object containing updated fields,
     *            provided in the request body.
     * @return an updated car if successful, or an empty response if not found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Car> partialUpdateCar(@PathVariable Long id, @RequestBody Car car) {
        return ResponseEntity.of(carService.partialUpdateCar(id, car));
    }

    /**
     * Deletes a car by its unique identifier.
     *
     * @param id The unique identifier of the car to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}