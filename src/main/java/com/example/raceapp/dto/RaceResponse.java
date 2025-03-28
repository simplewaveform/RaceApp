package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Set;

/**
 * Detailed DTO for race responses with full participant data.
 */
@Schema(description = "Detailed Race Response")
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Set<PilotResponse> getPilots() { return pilots; }
    public void setPilots(Set<PilotResponse> pilots) { this.pilots = pilots; }

    public Set<CarResponse> getCars() { return cars; }
    public void setCars(Set<CarResponse> cars) { this.cars = cars; }
}
