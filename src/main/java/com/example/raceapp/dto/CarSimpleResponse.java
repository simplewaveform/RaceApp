package com.example.raceapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Simplified DTO for car references in other responses.
 */
@Getter
@Setter
public class CarSimpleResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer power;
}