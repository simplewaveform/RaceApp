package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Detailed DTO for pilot responses with associated cars.
 */
@Schema(description = "Detailed Pilot Response")
@Getter
@Setter
public class PilotResponse {
    @Schema(description = "Pilot ID", example = "1")
    private Long id;

    @Schema(description = "Pilot's name", example = "John Doe")
    private String name;

    @Schema(description = "Pilot's age", example = "30")
    private Integer age;

    @Schema(description = "Pilot's experience in years", example = "5")
    private Integer experience;

    @Schema(description = "List of associated cars")
    private List<CarSimpleResponse> cars;
}
