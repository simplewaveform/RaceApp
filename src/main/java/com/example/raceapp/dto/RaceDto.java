package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * DTO for creating or updating a race.
 */
@Getter
@Setter
@Schema(description = "Data Transfer Object for race creation and updates")
public class RaceDto {

    @Schema(description = "Race name", example = "Grand Prix 2025")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Year of the race", example = "2025")
    @Min(value = 1900, message = "Year must be after 1900")
    private Integer year;

    @Schema(description = "Set of pilot IDs participating in the race", example = "[1, 2, 3]")
    @NotEmpty(message = "Pilot IDs cannot be empty")
    private Set<Long> pilotIds;

    @Schema(description = "Set of car IDs participating in the race", example = "[10, 20, 30]")
    @NotEmpty(message = "Car IDs cannot be empty")
    private Set<Long> carIds;
}
