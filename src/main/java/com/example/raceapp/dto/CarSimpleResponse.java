package com.example.raceapp.dto;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Simplified DTO for car references in other responses.
 */
@Getter
@Setter
public class CarSimpleResponse {
    private Long id;
    private String brand;
    private String model;
    private Integer power;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CarSimpleResponse that = (CarSimpleResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}