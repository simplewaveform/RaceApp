package com.example.raceapp.controller;

import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.dto.RaceResponse;
import com.example.raceapp.service.RaceService;
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

@Tag(name = "Races", description = "API for managing races")
@RestController
@RequestMapping("/races")
public class RaceController {
    private final RaceService raceService;

    public RaceController(RaceService raceService) {
        this.raceService = raceService;
    }

    @Operation(
            summary = "Create a race",
            description = "Creates a new race with specified parameters",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Race created",
                            content = @Content(schema = @Schema(implementation = RaceResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping
    public ResponseEntity<RaceResponse> createRace(@Valid @RequestBody RaceDto raceDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(raceService.createRace(raceDto));
    }

    @Operation(
            summary = "Get all races",
            description = "Returns paginated list of all races",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Races retrieved",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping
    public ResponseEntity<Page<RaceResponse>> getAllRaces(Pageable pageable) {
        return ResponseEntity.ok(raceService.getAllRaces(pageable));
    }

    @Operation(
            summary = "Get race by ID",
            description = "Returns a single race with full details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Race found",
                            content = @Content(schema = @Schema(implementation = RaceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Race not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<RaceResponse> getRaceById(
            @Parameter(description = "ID of race to return", required = true, example = "1")
            @PathVariable Long id) {
        Optional<RaceResponse> race = raceService.getRaceById(id);
        return race.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update race by ID",
            description = "Fully updates an existing race",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Race updated",
                            content = @Content(schema = @Schema(implementation = RaceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Race not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<RaceResponse> updateRace(
            @Parameter(description = "ID of race to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody RaceDto raceDto) {
        Optional<RaceResponse> updatedRace = raceService.updateRace(id, raceDto);
        return updatedRace.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Partially update race by ID",
            description = "Updates specific fields of an existing race",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Race partially updated",
                            content = @Content(schema = @Schema(implementation = RaceResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Race not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<RaceResponse> partialUpdateRace(
            @Parameter(description = "ID of race to update", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Optional<RaceResponse> updatedRace = raceService.partialUpdateRace(id, updates);
        return updatedRace.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete race by ID",
            description = "Permanently removes a race from the system",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Race deleted"),
                    @ApiResponse(responseCode = "404", description = "Race not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRace(@PathVariable Long id) {
        raceService.deleteRace(id);
    }
}