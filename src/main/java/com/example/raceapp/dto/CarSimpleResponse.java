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
    @Schema(description = "Car ID", example = "10")
    private Long id;

    @Schema(description = "Car brand", example = "Toyota")
    private String brand;

    @Schema(description = "Car model", example = "Corolla")
    private String model;

    @Schema(description = "Engine power in horsepower", example = "200")
    private Integer power;
}
