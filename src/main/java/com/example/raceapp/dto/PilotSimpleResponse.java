package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Simplified DTO for pilot references in other responses.
 */
@Schema(description = "Simplified Pilot Response")
public class PilotSimpleResponse {
    @Schema(description = "Pilot ID", example = "3")
    private Long id;

    @Schema(description = "Pilot's name", example = "Max Verstappen")
    private String name;

    @Schema(description = "Pilot's experience in years", example = "5")
    private Integer experience;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

}
