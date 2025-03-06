package com.example.raceapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Dto for transferring car data between layers.
 */
@Getter
@Setter
public class CarDto {
    private Long id;
    private String brand;
    private String model;
    private Integer power;
    private Long ownerId;
}