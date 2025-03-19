package com.example.raceapp.controller;

import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.service.PilotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing pilot entities.
 * Provides CRUD operations for pilots in the system.
 */
@Tag(name = "Pilots", description = "API for managing pilots")
@RestController
@RequestMapping("/pilots")
@RequiredArgsConstructor
public class PilotController {
    public static final String PILOT_NOT_FOUND = "Pilot not found";
    private final PilotService pilotService;

    /**
     * Creates a new pilot.
     *
     * @param pilotDto the pilot data to create a new pilot
     * @return ResponseEntity containing the created pilot's data
     */
    @Operation(
            summary = "Create a new pilot",
            description = "Creates a new pilot with the specified parameters.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Pilot data",
                    content = @Content(
                            schema = @Schema(example = "{ \"name\": \"John Doe\","
                                    + "\"age\": 30, \"experience\": 5 }")
                    )
            ),
            responses = {
                @ApiResponse(responseCode = "201", description = "Pilot created",
                            content = @Content(schema = @Schema(implementation =
                                    PilotResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(example = "{ \"error\": "
                                    + "\"Invalid input data\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Unexpected error occurred\" }")))
            }
    )
    @PostMapping
    public ResponseEntity<PilotResponse> createPilot(@Valid @RequestBody PilotDto pilotDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilotDto));
    }

    /**
     * Retrieves a paginated list of pilots with optional filters.
     *
     * @param name       optional filter by pilot name
     * @param age        optional filter by pilot age
     * @param experience optional filter by pilot experience
     * @param pageable   pagination details
     * @return ResponseEntity containing the list of pilots with pagination
     */
    @Operation(
            summary = "Get pilots with optional filters",
            description = "Returns a paginated list of pilots with optional filters"
                    + " such as name, age, and experience.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Pilots retrieved",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Unexpected error occurred\" }")))
            }
    )
    @GetMapping
    public ResponseEntity<Page<PilotResponse>> getPilots(
            @Parameter(description = "Filter by name", example = "John")
            @RequestParam(required = false) String name,
            @Parameter(description = "Filter by age", example = "30")
            @RequestParam(required = false) Integer age,
            @Parameter(description = "Filter by experience", example = "5")
            @RequestParam(required = false) Integer experience,
            Pageable pageable) {
        return ResponseEntity.ok(pilotService.searchPilotsWithPagination(name,
                age, experience, pageable));
    }

    /**
     * Retrieves pilots filtered by car brand.
     *
     * @param brand    the car brand to filter pilots by
     * @param pageable pagination details
     * @return ResponseEntity containing the filtered list of pilots
     */
    @Operation(
            summary = "Get pilots by car brand",
            description = "Returns pilots owning cars of specified brand",
            responses = {
                @ApiResponse(responseCode = "200", description = "Pilots retrieved",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(responseCode = "400", description = "Invalid brand parameter",
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Invalid brand parameter\" }")))
            }
    )
    @GetMapping("/by-car-brand")
    public ResponseEntity<Page<PilotResponse>> getPilotsByCarBrand(
            @RequestParam String brand, Pageable pageable) {
        return ResponseEntity.ok(pilotService.getPilotsByCarBrandNative(brand, pageable));
    }

    /**
     * Retrieves a single pilot by their ID.
     *
     * @param id the ID of the pilot to retrieve
     * @return ResponseEntity containing the pilot's details, or a 404 if not found
     */
    @Operation(
            summary = "Get a pilot by ID",
            description = "Returns a single pilot's details identified by the pilot's ID.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Pilot found",
                            content = @Content(schema = @Schema(implementation =
                                    PilotResponse.class))),
                @ApiResponse(responseCode = "404", description = PILOT_NOT_FOUND,
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Pilot not found\" }")))
            }
    )
    @GetMapping("/{id}")
    public PilotResponse getPilotById(@PathVariable Long id) {
        return pilotService.getPilotById(id)
                .orElseThrow(() -> new NotFoundException(PILOT_NOT_FOUND));
    }

    /**
     * Fully updates an existing pilot.
     *
     * @param id        the ID of the pilot to update
     * @param pilotDto  the updated pilot data
     * @return ResponseEntity containing the updated pilot's data, or a 404 if not found
     */
    @Operation(
            summary = "Update pilot by ID",
            description = "Fully updates the details of an existing pilot identified by the ID.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Pilot updated",
                            content = @Content(schema = @Schema(implementation =
                                    PilotResponse.class))),
                @ApiResponse(responseCode = "404", description = PILOT_NOT_FOUND,
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Pilot not found\" }"))),
                @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Invalid input data\" }")))
            }
    )
    @PutMapping("/{id}")
    public PilotResponse updatePilot(@PathVariable Long id, @Valid @RequestBody PilotDto pilotDto) {
        return pilotService.updatePilot(id, pilotDto)
                .orElseThrow(() -> new NotFoundException(PILOT_NOT_FOUND));
    }

    /**
     * Deletes a pilot by their ID.
     *
     * @param id the ID of the pilot to delete
     */
    @Operation(
            summary = "Delete a pilot by ID",
            description = "Deletes a pilot identified by the ID.",
            responses = {
                @ApiResponse(responseCode = "204", description = "Pilot deleted"),
                @ApiResponse(responseCode = "404", description = PILOT_NOT_FOUND,
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Pilot not found\" }")))
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}
