package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO for creating or updating a car.
 */
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

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public int getPower() { return power; }
    public void setPower(int power) { this.power = power; }

    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}
