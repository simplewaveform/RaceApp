package com.example.raceapp.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.Set;

/**
 * Detailed DTO for race responses with full participant data.
 */
@Getter
@Setter
public class RaceResponse {
    private Long id;
    private String name;
    private Integer year;
    private Set<PilotResponse> pilots;
    private Set<CarResponse> cars;
}