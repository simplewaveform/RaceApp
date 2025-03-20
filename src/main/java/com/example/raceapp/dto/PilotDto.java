package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating or updating a pilot.
 */
@Getter
@Setter
@Schema(description = "Data Transfer Object for pilot creation and updates")
public class PilotDto {

    @Schema(description = "Pilot's name", example = "Max Verstappen")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Pilot's age", example = "30")
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    @Schema(description = "Pilot's experience in years", example = "5")
    @PositiveOrZero(message = "Experience cannot be negative")
    private Integer experience;
}
