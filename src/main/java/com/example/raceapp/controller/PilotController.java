package com.example.raceapp.controller;

import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.service.PilotService;
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

@Tag(name = "Pilots", description = "API for managing pilots")
@RestController
@RequestMapping("/pilots")
public class PilotController {
    private final PilotService pilotService;

    public PilotController(PilotService pilotService) {
        this.pilotService = pilotService;
    }

    @Operation(
            summary = "Create a pilot",
            description = "Creates a new pilot with specified parameters",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Pilot created",
                            content = @Content(schema = @Schema(implementation = PilotResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PostMapping
    public ResponseEntity<PilotResponse> createPilot(@Valid @RequestBody PilotDto pilotDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pilotService.createPilot(pilotDto));
    }

    @Operation(
            summary = "Get pilots with filters",
            description = "Returns paginated list of pilots with optional filters",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pilots retrieved",
                            content = @Content(schema = @Schema(implementation = Page.class))),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
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
        return ResponseEntity.ok(pilotService.searchPilotsWithPagination(name, age, experience, pageable));
    }

    @Operation(
            summary = "Get pilot by ID",
            description = "Returns a single pilot with full details",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pilot found",
                            content = @Content(schema = @Schema(implementation = PilotResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Pilot not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<PilotResponse> getPilotById(
            @Parameter(description = "ID of pilot to return", required = true, example = "1")
            @PathVariable Long id) {
        Optional<PilotResponse> pilot = pilotService.getPilotById(id);
        return pilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Update pilot by ID",
            description = "Fully updates an existing pilot",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pilot updated",
                            content = @Content(schema = @Schema(implementation = PilotResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Pilot not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<PilotResponse> updatePilot(
            @Parameter(description = "ID of pilot to update", required = true, example = "1")
            @PathVariable Long id,
            @Valid @RequestBody PilotDto pilotDto) {
        Optional<PilotResponse> updatedPilot = pilotService.updatePilot(id, pilotDto);
        return updatedPilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Partially update pilot by ID",
            description = "Updates specific fields of an existing pilot",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Pilot partially updated",
                            content = @Content(schema = @Schema(implementation = PilotResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Pilot not found"),
                    @ApiResponse(responseCode = "400", description = "Invalid input"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<PilotResponse> partialUpdatePilot(
            @Parameter(description = "ID of pilot to update", required = true, example = "1")
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        Optional<PilotResponse> updatedPilot = pilotService.partialUpdatePilot(id, updates);
        return updatedPilot.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(
            summary = "Delete pilot by ID",
            description = "Permanently removes a pilot from the system",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Pilot deleted"),
                    @ApiResponse(responseCode = "404", description = "Pilot not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePilot(@PathVariable Long id) {
        pilotService.deletePilot(id);
    }
}