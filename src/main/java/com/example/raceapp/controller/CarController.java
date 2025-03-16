package com.example.raceapp.controller;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.service.CarService;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
 * REST controller for managing car-related operations such as creation,
 * retrieval, updating, and deletion.
 *
 * <p>Handles HTTP requests related to car data and provides JSON responses
 * with relevant details.</p>
 */
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    /**
     * Constructs a {@code CarController} with the provided {@code CarService}.
     *
     * @param carService the service for managing car operations.
     */
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Creates a new car and returns the created car's data.
     *
     * @param carDto DTO containing car data.
     * @return The created car's data with HTTP status 201 (Created).
     */
    @PostMapping
    public ResponseEntity<CarResponse> createCar(@RequestBody CarDto carDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(carDto));
    }

    /**
     * Retrieves a list of cars filtered by optional parameters such as
     * brand, model, power, or owner ID.
     *
     * @param brand   Optional filter for car brand.
     * @param model   Optional filter for car model.
     * @param power   Optional filter for car power.
     * @param ownerId Optional filter for car owner ID.
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
     * Retrieves a car by its unique identifier.
     *
     * @param id The unique ID of the car.
     * @return The car's data if found, or HTTP status 404 (Not Found) if not.
     */
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(@PathVariable Long id) {
        Optional<CarResponse> car = carService.getCarById(id);
        return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Retrieves a paginated list of cars filtered by their power value.
     *
     * @param power    The car's power value for filtering.
     * @param pageable Pageable parameters for pagination.
     * @return A paginated list of cars matching the specified power value.
     */
    @GetMapping("/by-power")
    public ResponseEntity<Page<CarResponse>> getCarsByPower(
            @RequestParam Integer power,
            Pageable pageable) {
        return ResponseEntity.ok(carService.getCarsByPower(power, pageable));
    }

    /**
     * Updates an existing car's data by ID.
     *
     * @param id     The ID of the car to update.
     * @param carDto DTO containing the updated car data.
     * @return The updated car's data, or HTTP status 404 if the car is not found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(@PathVariable Long id,
                                                 @RequestBody CarDto carDto) {
        Optional<CarResponse> updatedCar = carService.updateCar(id, carDto);
        return updatedCar.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity
                .notFound().build());
    }

    /**
     * Partially updates an existing car's data by ID.
     *
     * @param id      The ID of the car to update.
     * @param updates Map containing the fields to update and their new values.
     * @return The updated car's data, or HTTP status 404 if the car is not found.
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
     * Deletes a car by its unique identifier.
     *
     * @param id The ID of the car to delete.
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}
