package com.example.raceapp.dto;

import java.util.Set;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

/**
 * Dto for transferring race data between layers.
 */
@Getter
@Setter
public class RaceDto {
    @NotBlank(message = "Name is required")
    private String name;

    @Min(value = 1900, message = "Year must be after 1900")
    private Integer year;

    @NotEmpty(message = "Pilot IDs cannot be empty")
    private Set<Long> pilotIds;

    @NotEmpty(message = "Car IDs cannot be empty")
    private Set<Long> carIds;
}