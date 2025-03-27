package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(description = "Data Transfer Object for pilot creation and updates")
public class PilotDto {

    @Schema(description = "Pilot's name", example = "Max Verstappen")
    @NotBlank(message = "Name is required")
    private String name;

    @Schema(description = "Pilot's age", example = "30")
    @Min(value = 18, message = "Age must be at least 18")
    private Integer age;

    @Schema(description = "Pilot's experience in years", example = "5")
    @PositiveOrZero(message = "Experience cannot be negative")
    private Integer experience;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
}