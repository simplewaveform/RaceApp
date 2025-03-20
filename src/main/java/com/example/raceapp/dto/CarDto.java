package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for creating or updating a car.
 */
@Getter
@Setter
@Schema(description = "Data Transfer Object for car creation and updates")
public class CarDto {

    @Schema(description = "Car brand", example = "Red Bull")
    @NotBlank(message = "Brand is required")
    private String brand;

    @Schema(description = "Car model", example = "RB25")
    @NotBlank(message = "Model is required")
    private String model;

    @Schema(description = "Engine power in horsepower", example = "980")
    @Positive(message = "Power must be a positive number")
    private Integer power;

    @Schema(description = "Owner ID (pilot ID)", example = "3")
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
}
