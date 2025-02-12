package com.example.raceapp.controller;

import com.example.raceapp.model.Car;
import com.example.raceapp.service.CarService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing cars.
 */
@RestController
@RequestMapping("/car")
public class CarController {
    private final CarService carService;

    /**
     * Constructor for CarController.
     *
     * @param carService service for handling car-related operations
     */
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Creates a new car.
     *
     * @param car the {@link Car} object received in the request body
     * @return a {@link ResponseEntity} containing the created car and HTTP status 201 Created
     */
    @PostMapping("/add")
    public ResponseEntity<Car> createCar(@RequestBody Car car) {
        Car createdCar = carService.createCar(car);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdCar);
    }

    /**
     * Retrieves all cars. Optionally filters by brand if provided.
     *
     * @param brand the brand of the cars to filter by (optional)
     * @return a {@link ResponseEntity} containing a list of cars and HTTP status 200 OK
     */
    @GetMapping("/all")
    public ResponseEntity<List<Car>> getAllCars(@RequestParam(required = false) String brand) {
        if (brand != null) {
            return ResponseEntity.ok(carService.getCarsByBrand(brand));
        } else {
            return ResponseEntity.ok(carService.getAllCars());
        }
    }

    /**
     * Retrieves a car by its ID.
     *
     * @param id the car ID
     * @return a {@link ResponseEntity} containing the found car and HTTP status 200 OK,
     *         or HTTP status 404 Not Found if the car does not exist
     */
    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable Long id) {
        Optional<Car> car = Optional.ofNullable(carService.getCarById(id));
        return car.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a car by its ID.
     *
     * @param id the car ID
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}