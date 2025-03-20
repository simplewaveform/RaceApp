package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Simplified DTO for car references in other responses.
 */
@Schema(description = "Simplified Car Response")
@Getter
@Setter
public class CarSimpleResponse {
    @Schema(description = "Car ID", example = "3")
    private Long id;

    @Schema(description = "Car brand", example = "Red Bull")
    private String brand;

    @Schema(description = "Car model", example = "RB25")
    private String model;

    @Schema(description = "Engine power in horsepower", example = "980")
    private Integer power;
}
