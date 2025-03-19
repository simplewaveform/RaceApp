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
 * Controller for managing car entities.
 * Provides CRUD operations for cars in the system.
 */
@Tag(name = "Cars", description = "API for managing cars")
@RestController
@RequestMapping("/cars")
public class CarController {
    private final CarService carService;

    /**
     * Constructor for CarController.
     *
     * @param carService Service layer to handle car-related operations.
     */
    public CarController(CarService carService) {
        this.carService = carService;
    }

    /**
     * Creates a new car.
     *
     * @param carDto The car data to be created.
     * @return ResponseEntity with created car data.
     */
    @Operation(
            summary = "Create a car",
            description = "Creates a new car with specified parameters",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Car data",
                    content = @Content(
                            schema = @Schema(example = "{ \"brand\": \"Toyota\", \"model\": "
                                    + "\"Corolla\", \"power\": 150, \"ownerId\": 1 }")
                    )
            ),
            responses = {
                @ApiResponse(responseCode = "201", description = "Car created",
                            content = @Content(schema = @Schema(implementation =
                                    CarResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping
    public ResponseEntity<CarResponse> createCar(@Valid @RequestBody CarDto carDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(carService.createCar(carDto));
    }

    /**
     * Returns cars with power greater than the specified value.
     *
     * @param minPower The minimum power of the cars.
     * @param pageable Pagination details.
     * @return ResponseEntity with paginated list of cars.
     */
    @Operation(
            summary = "Get cars by minimum power",
            description = "Returns a paginated list of cars with power greater "
                    + "than the specified value",
            parameters = {
                @Parameter(name = "minPower", description = "Minimum power of the car",
                            required = true, example = "200")
            },
            responses = {
                @ApiResponse(responseCode = "200", description = "Cars retrieved",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/by-power")
    public ResponseEntity<Page<CarResponse>> getCarsByPower(
            @RequestParam Integer minPower, Pageable pageable) {
        return ResponseEntity.ok(carService.getCarsByPower(minPower, pageable));
    }

    /**
     * Returns a list of cars with optional filters.
     *
     * @param brand The brand of the car.
     * @param model The model of the car.
     * @param power The power of the car.
     * @param ownerId The owner ID of the car.
     * @param pageable Pagination details.
     * @return ResponseEntity with paginated list of cars.
     */
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
        return ResponseEntity.ok(carService.searchCarsWithPagination(brand, model, power,
                ownerId, pageable));
    }

    /**
     * Returns a single car by its ID.
     *
     * @param id The ID of the car to return.
     * @return ResponseEntity with car details or a 404 if not found.
     */
    @Operation(
            summary = "Get car by ID",
            description = "Returns a single car with full details",
            responses = {
                @ApiResponse(responseCode = "200", description = "Car found",
                            content = @Content(schema = @Schema(implementation =
                                    CarResponse.class))),
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

    /**
     * Fully updates a car by its ID.
     *
     * @param id The ID of the car to update.
     * @param carDto The car data to update.
     * @return ResponseEntity with updated car details or a 404 if not found.
     */
    @Operation(
            summary = "Update car by ID",
            description = "Fully updates an existing car",
            responses = {
                @ApiResponse(responseCode = "200", description = "Car updated",
                            content = @Content(schema = @Schema(implementation =
                                    CarResponse.class))),
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
        return updatedCar.map(ResponseEntity::ok).orElseGet(() ->
                ResponseEntity.notFound().build());
    }

    /**
     * Partially updates a car by its ID.
     *
     * @param id The ID of the car to update.
     * @param updates A map of fields to update.
     * @return ResponseEntity with updated car details or a 404 if not found.
     */
    @Operation(
            summary = "Partially update car by ID",
            description = "Updates specific fields of an existing car",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Fields to update",
                    content = @Content(
                            schema = @Schema(example = "{ \"brand\": \"Toyota\", "
                                    + "\"model\": \"Corolla\", \"power\": 150 }")
                    )
            ),
            responses = {
                @ApiResponse(responseCode = "200", description = "Car partially updated",
                            content = @Content(schema = @Schema(implementation =
                                    CarResponse.class))),
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
        return updatedCar.map(ResponseEntity::ok).orElseGet(() ->
                ResponseEntity.notFound().build());
    }

    /**
     * Deletes a car by its ID.
     *
     * @param id The ID of the car to delete.
     */
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
