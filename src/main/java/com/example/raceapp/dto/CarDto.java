package com.example.raceapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * Dto for transferring car data between layers.
 */
@Getter
@Setter
public class CarDto {
    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @Positive(message = "Power must be positive")
    private Integer power;

    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}