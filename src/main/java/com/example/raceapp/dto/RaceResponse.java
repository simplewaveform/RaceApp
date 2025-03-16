package com.example.raceapp.dto;

import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Detailed DTO for race responses with full participant data.
 */
@Getter
@Setter
public class RaceResponse {
    private Long id;
    private String name;
    private Integer year;
    private Set<PilotResponse> pilots;
    private Set<CarResponse> cars;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        RaceResponse that = (RaceResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}