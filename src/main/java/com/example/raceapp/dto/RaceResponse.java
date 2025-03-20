package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Detailed DTO for race responses with full participant data.
 */
@Schema(description = "Detailed Race Response")
@Getter
@Setter
public class RaceResponse {
    @Schema(description = "Race ID", example = "1")
    private Long id;

    @Schema(description = "Race name", example = "Grand Prix Miami")
    private String name;

    @Schema(description = "Year of the race", example = "2025")
    private Integer year;

    @Schema(description = "List of participating pilots")
    private Set<PilotResponse> pilots;

    @Schema(description = "List of participating cars")
    private Set<CarResponse> cars;
}
