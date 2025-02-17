package com.example.raceapp.controller;

import com.example.raceapp.model.Car;
import com.example.raceapp.service.CarService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
 * Controller for managing Car entities.
 * Provides CRUD operations for Car.
 */
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    /**
     * Constructor for the CarController class.
     *
     * @param carService The CarService instance to be injected into this controller.
     */
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Creates a new car entity.
     *
     * @param car the car object received in the request body.
     * @return the created car with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(car));
    }

    /**
     * Retrieves a car by its ID.
     *
     * @param id the ID of the car.
     * @return the car if found, or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        return ResponseEntity.of(carService.getCarById(id));
    }

    /**
     * Retrieves all cars.
     *
     * @return a list of all cars.
     */
    @GetMapping("/all")
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    /**
     * Retrieves cars with requested brand.
     *
     * @return all cars of requested brand.
     */
    @GetMapping
    public ResponseEntity<List<Car>> getCarsByBrand(@RequestParam String brand) {
        return ResponseEntity.ok(carService.getCarsByBrand(brand));
    }

    /**
     * Fully updates a car entity by its ID.
     *
     * @param id the ID of the car to update.
     * @param car the updated car data.
     * @return the updated car entity.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Car> updateCar(@PathVariable Long id, @RequestBody Car car) {
        return ResponseEntity.of(carService.updateCar(id, car));
    }

    /**
     * Partially updates a car entity by its ID.
     *
     * @param id the ID of the car to update.
     * @param car the partial car data.
     * @return the updated car entity.
     */
    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Car> partialUpdateCar(@PathVariable Long id, @RequestBody Car car) {
        return ResponseEntity.of(carService.partialUpdateCar(id, car));
    }

    /**
     * Deletes a car by its ID.
     *
     * @param id the ID of the car to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}