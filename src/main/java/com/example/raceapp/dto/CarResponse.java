package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Detailed DTO for car responses with owner information.
 */
@Schema(description = "Detailed Car Response")
@Getter
@Setter
public class CarResponse {
    @Schema(description = "Car ID", example = "1")
    private Long id;

    @Schema(description = "Car brand", example = "Toyota")
    private String brand;

    @Schema(description = "Car model", example = "Corolla")
    private String model;

    @Schema(description = "Car power in horsepower", example = "150")
    private Integer power;

    @Schema(description = "Owner information")
    private PilotSimpleResponse owner;
}
