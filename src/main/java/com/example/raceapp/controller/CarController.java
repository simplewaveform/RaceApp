package com.example.raceapp.controller;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.service.CarService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
 * REST controller for managing cars.
 */
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    /**
     * Constructs a CarController with the provided CarService.
     *
     * @param carService the service for managing car operations.
     */
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Creates a new car.
     *
     * @param carDto Dto containing car data.
     * @return Created car Dto with HTTP 201.
     */
    @PostMapping
    public ResponseEntity<CarResponse> createCar(@RequestBody CarDto carDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(carDto));
    }

    /**
     * Retrieves cars by optional filters.
     *
     * @param brand Car brand filter.
     * @param model Car model filter.
     * @param power Car power filter.
     * @param ownerId Car owner ID filter.
     * @return List of filtered cars.
     */
    @GetMapping
    public ResponseEntity<List<CarResponse>> getCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer power,
            @RequestParam(required = false) Long ownerId) {
        return ResponseEntity.ok(carService.searchCars(brand, model, power, ownerId));
    }

    /**
     * Retrieves a car by ID.
     *
     * @param id Car ID.
     * @return Car Dto or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) {
        Optional<CarResponse> car = carService.getCarById(id);
        return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing car.
     *
     * @param id     Car ID.
     * @param carDto Updated car data.
     * @return Updated car Dto or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(@PathVariable Long id, @RequestBody CarDto carDto) {
        Optional<CarResponse> updatedCar = carService.updateCar(id, carDto);
        return updatedCar.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity
                                                                  .notFound().build());
    }

    /**
     * Partially updates a car by ID.
     *
     * @param id      the ID of the car to update.
     * @param updates map of fields to update.
     * @return updated car DTO, or 404 if not found.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<CarResponse> partialUpdateCar(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Optional<CarResponse> updatedCar = carService.partialUpdateCar(id, updates);
        return updatedCar
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a car by ID.
     *
     * @param id Car ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}