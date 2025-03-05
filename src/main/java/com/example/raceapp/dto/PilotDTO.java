package com.example.raceapp.dto;

/**
 * DTO for transferring pilot data between layers.
 */
public class PilotDTO {
    private Long id;
    private String name;
    private Integer age;
    private Integer experience;

    // Геттеры и сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }
}