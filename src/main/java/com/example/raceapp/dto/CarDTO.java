package com.example.raceapp.dto;

/**
 * DTO for transferring car data between layers.
 */
public class CarDTO {
    private Long id;
    private String brand;
    private String model;
    private Integer power;
    private Long ownerId;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    public Integer getPower() { return power; }
    public void setPower(Integer power) { this.power = power; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}