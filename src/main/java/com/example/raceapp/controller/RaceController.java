package com.example.raceapp.controller;

import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.dto.RaceResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.service.RaceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing race entities.
 * Provides CRUD operations for races in the system.
 */
@Tag(name = "Races", description = "API for managing races")
@RestController
@RequestMapping("/races")
public class RaceController {
    public static final String RACE_NOT_FOUND = "Race not found";
    private final RaceService raceService;

    @Autowired
    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    /**
     * Creates a new race with the specified parameters.
     *
     * @param raceDto the data transfer object containing race information
     * @return the created race with its ID
     */
    @Operation(
            summary = "Create a race",
            description = "Creates a new race with specified parameters",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Race data",
                    content = @Content(
                            schema = @Schema(example = "{ \"name\": \"Grand Prix Miami\","
                                    + "\"year\": 2025, \"pilotIds\": [1,3], \"carIds\": [1,3] }")
                    )
            ),
            responses = {
                @ApiResponse(responseCode = "201", description = "Race created",
                            content = @Content(schema = @Schema(implementation =
                                    RaceResponse.class))),
                @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Invalid input data\" }"))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Unexpected error occurred\" }")))
            }
    )
    @PostMapping
    public ResponseEntity<RaceResponse> createRace(@Valid @RequestBody RaceDto raceDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(raceDto));
    }

    /**
     * Retrieves all races with pagination support.
     *
     * @param pageable the pagination parameters
     * @return a paginated list of races
     */
    @Operation(
            summary = "Get all races",
            description = "Returns a paginated list of all races",
            responses = {
                @ApiResponse(responseCode = "200", description = "Races retrieved",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Unexpected error occurred\" }")))
            }
    )
    @GetMapping
    public ResponseEntity<Page<RaceResponse>> getAllRaces(Pageable pageable) {
        return ResponseEntity.ok(raceService.getAllRaces(pageable));
    }

    /**
     * Retrieves a single race by its ID.
     *
     * @param id the ID of the race to retrieve
     * @return the race with the specified ID
     */
    @Operation(
            summary = "Get race by ID",
            description = "Returns a single race with full details",
            responses = {
                @ApiResponse(responseCode = "200", description = "Race found",
                            content = @Content(schema = @Schema(implementation =
                                    RaceResponse.class))),
                @ApiResponse(responseCode = "404", description = RACE_NOT_FOUND,
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Race not found\" }")))
            }
    )
    @GetMapping("/{id}")
    public RaceResponse getRaceById(
            @Parameter(description = "ID of the race to return", required = true, example = "1")
            @PathVariable Long id) {
        return raceService.getRaceById(id)
                .orElseThrow(() -> new NotFoundException(RACE_NOT_FOUND));
    }

    /**
     * Fully updates an existing race by its ID.
     *
     * @param id the ID of the race to update
     * @param raceDto the data transfer object containing updated race information
     * @return the updated race
     */
    @Operation(
            summary = "Update race by ID",
            description = "Fully updates an existing race",
            responses = {
                @ApiResponse(responseCode = "200", description = "Race updated",
                            content = @Content(schema = @Schema(implementation =
                                    RaceResponse.class))),
                @ApiResponse(responseCode = "404", description = RACE_NOT_FOUND,
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Race not found\" }"))),
                @ApiResponse(responseCode = "400", description = "Invalid input",
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Invalid input data\" }")))
            }
    )
    @PutMapping("/{id}")
    public RaceResponse updateRace(
            @Parameter(description = "ID of the race to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RaceDto raceDto) {
        return raceService.updateRace(id, raceDto)
                .orElseThrow(() -> new NotFoundException(RACE_NOT_FOUND));
    }

    /**
     * Deletes a race by its ID.
     *
     * @param id the ID of the race to delete
     */
    @Operation(
            summary = "Delete race by ID",
            description = "Permanently removes a race from the system",
            responses = {
                @ApiResponse(responseCode = "204", description = "Race deleted"),
                @ApiResponse(responseCode = "404", description = RACE_NOT_FOUND,
                            content = @Content(schema = @Schema(example = "{ \"error\":"
                                    + "\"Race not found\" }")))
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }
}
