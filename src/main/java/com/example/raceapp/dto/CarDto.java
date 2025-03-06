package com.example.raceapp.dto;

import lombok.Data;

/**
 * Dto for transferring car data between layers.
 */
@Data
public class CarDto {
    private Long id;
    private String brand;
    private String model;
    private Integer power;
    private Long ownerId;
}