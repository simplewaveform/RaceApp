package com.example.raceapp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

/**
 * Dto for transferring pilot data between layers.
 */
@Getter
@Setter
public class PilotDto {
    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    @PositiveOrZero(message = "Experience cannot be negative")
    private Integer experience;
}