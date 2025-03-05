package com.example.raceapp.controller;

import com.example.raceapp.dto.CarDTO;
import com.example.raceapp.service.CarService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST controller for managing cars.
 */
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Creates a new car.
     * @param carDTO DTO containing car data.
     * @return Created car DTO with HTTP 201.
     */
    @PostMapping
    public ResponseEntity<CarDTO> createCar(@RequestBody CarDTO carDTO) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(carDTO));
    }

    /**
     * Retrieves cars by optional filters.
     * @param brand Car brand filter.
     * @param model Car model filter.
     * @param power Car power filter.
     * @param ownerId Car owner ID filter.
     * @return List of filtered cars.
     */
    @GetMapping
    public ResponseEntity<List<CarDTO>> getCars(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) Integer power,
            @RequestParam(required = false) Long ownerId) {
        return ResponseEntity.ok(carService.searchCars(brand, model, power, ownerId));
    }

    /**
     * Retrieves a car by ID.
     * @param id Car ID.
     * @return Car DTO or 404 if not found.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarDTO> getCarById(@PathVariable Long id) {
        Optional<CarDTO> car = carService.getCarById(id);
        return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Updates an existing car.
     * @param id Car ID.
     * @param carDTO Updated car data.
     * @return Updated car DTO or 404 if not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarDTO> updateCar(@PathVariable Long id, @RequestBody CarDTO carDTO) {
        Optional<CarDTO> updatedCar = carService.updateCar(id, carDTO);
        return updatedCar.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Deletes a car by ID.
     * @param id Car ID.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}