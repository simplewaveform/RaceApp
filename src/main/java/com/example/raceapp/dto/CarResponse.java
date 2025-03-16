package com.example.raceapp.dto;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Detailed DTO for car responses with owner information.
 */
@Getter
@Setter
public class CarResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer power;
    private PilotSimpleResponse owner;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CarResponse that = (CarResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}