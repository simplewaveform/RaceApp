package com.example.raceapp.dto;

import java.util.Set;
import lombok.Getter;
import lombok.Setter;

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