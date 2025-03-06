package com.example.raceapp.dto;

import lombok.Data;

/**
 * Dto for transferring pilot data between layers.
 */
@Data
public class PilotDto {
    private Long id;
    private String name;
    private Integer age;
    private Integer experience;
}