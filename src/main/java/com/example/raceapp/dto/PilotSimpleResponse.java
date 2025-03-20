package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

/**
 * Simplified DTO for pilot references in other responses.
 */
@Schema(description = "Simplified Pilot Response")
@Getter
@Setter
public class PilotSimpleResponse {
    @Schema(description = "Pilot ID", example = "3")
    private Long id;

    @Schema(description = "Pilot's name", example = "Max Verstappen")
    private String name;

    @Schema(description = "Pilot's experience in years", example = "5")
    private Integer experience;
}
