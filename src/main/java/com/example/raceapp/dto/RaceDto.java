package com.example.raceapp.dto;

import java.util.Set;

/**
 * Dto for transferring race data between layers.
 */
public class RaceDto {
    private Long id;
    private String name;
    private Integer year;
    private Set<Long> pilotIds;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Set<Long> getPilotIds() {
        return pilotIds;
    }

    public void setPilotIds(Set<Long> pilotIds) {
        this.pilotIds = pilotIds;
    }

}