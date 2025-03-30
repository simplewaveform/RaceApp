package com.example.raceapp.dto;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Detailed DTO for car responses with owner information.
 */
@Schema(description = "Detailed Car Response")
public class CarResponse {
    @Schema(description = "Car ID", example = "3")
    private Long id;

    @Schema(description = "Car brand", example = "Red Bull")
    private String brand;

    @Schema(description = "Car model", example = "RB25")
    private String model;

    @Schema(description = "Car power in horsepower", example = "950")
    private Integer power;

    @Schema(description = "Owner information")
    private PilotSimpleResponse owner;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Integer getPower() {
        return power;
    }

    public void setPower(Integer power) {
        this.power = power;
    }

    public PilotSimpleResponse getOwner() {
        return owner;
    }

    public void setOwner(PilotSimpleResponse owner) {
        this.owner = owner;
    }
}
