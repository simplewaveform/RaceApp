package com.example.raceapp.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * Dto for transferring pilot data between layers.
 */
@Getter
@Setter
public class PilotDto {
    private Long id;
    private String name;
    private Integer age;
    private Integer experience;
}