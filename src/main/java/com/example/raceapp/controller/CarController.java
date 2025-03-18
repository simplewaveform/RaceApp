package com.example.raceapp.controller;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Tag(name = "Cars", description = "API for managing cars")
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(
            summary = "Create a car",
            description = "Creates a new car with specified parameters",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Car created",
                            content = @Content(schema = @Schema(implementation = CarResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping
    public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarDto carDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(carDto));
    }

    @Operation(
            summary = "Get cars with filters",
            description = "Returns paginated list of cars with optional filters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cars retrieved",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public ResponseEntity<Page<CarResponse>> getCars(
            @Parameter(description = "Filter by brand", example = "Toyota")
            @RequestParam(required = false) String brand,
            @Parameter(description = "Filter by model", example = "Camry")
            @RequestParam(required = false) String model,
            @Parameter(description = "Filter by power", example = "200")
            @RequestParam(required = false) Integer power,
            @Parameter(description = "Filter by owner ID", example = "1")
            @RequestParam(required = false) Long ownerId,
            Pageable pageable) {
        return ResponseEntity.ok(carService.searchCarsWithPagination(brand, model, power, ownerId, pageable));
    }

    @Operation(
            summary = "Get car by ID",
            description = "Returns a single car with full details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car found",
                            content = @Content(schema = @Schema(implementation = CarResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Car not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<CarResponse> getCarById(
            @Parameter(description = "ID of car to return", required = true, example = "1")
            @PathVariable Long id) {
        Optional<CarResponse> car = carService.getCarById(id);
        return car.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update car by ID",
            description = "Fully updates an existing car",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car updated",
                            content = @Content(schema = @Schema(implementation = CarResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Car not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<CarResponse> updateCar(
            @Parameter(description = "ID of car to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody CarDto carDto) {
        Optional<CarResponse> updatedCar = carService.updateCar(id, carDto);
        return updatedCar.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Partially update car by ID",
            description = "Updates specific fields of an existing car",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Car partially updated",
                            content = @Content(schema = @Schema(implementation = CarResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Car not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<CarResponse> partialUpdateCar(
            @Parameter(description = "ID of car to update", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Optional<CarResponse> updatedCar = carService.partialUpdateCar(id, updates);
        return updatedCar.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete car by ID",
            description = "Permanently removes a car from the system",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Car deleted"),
                    @ApiResponse(responseCode = "404", description = "Car not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable Long id) {
        carService.deleteCar(id);
    }
}