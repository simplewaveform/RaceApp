package com.example.raceapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Detailed DTO for car responses with owner information.
 */
@Getter
@Setter
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer power;
    private PilotSimpleResponse owner;
}