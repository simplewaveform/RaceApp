package com.example.raceapp.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Dto for transferring race data between layers.
 */
@Getter
@Setter
public class RaceDto {
    private Long id;
    private String name;
    private Integer year;
    private Set<Long> pilotIds;
    private Set<Long> carIds;
}