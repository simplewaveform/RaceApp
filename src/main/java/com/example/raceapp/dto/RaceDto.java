package com.example.raceapp.dto;

import java.util.Set;
import lombok.Data;

/**
 * Dto for transferring race data between layers.
 */
@Data
public class RaceDto {
    private Long id;
    private String name;
    private Integer year;
    private Set<Long> pilotIds;
}