package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

/**
 * Detailed DTO for pilot responses with associated cars.
 */
@Schema(description = "Detailed Pilot Response")
public class PilotResponse {
    @Schema(description = "Pilot ID", example = "3")
    private Long id;

    @Schema(description = "Pilot's name", example = "Max Verstappen")
    private String name;

    @Schema(description = "Pilot's age", example = "30")
    private Integer age;

    @Schema(description = "Pilot's experience in years", example = "5")
    private Integer experience;

    @Schema(description = "List of associated cars")
    private List<CarSimpleResponse> cars;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public List<CarSimpleResponse> getCars() { return cars; }
    public void setCars(List<CarSimpleResponse> cars) { this.cars = cars; }

}
