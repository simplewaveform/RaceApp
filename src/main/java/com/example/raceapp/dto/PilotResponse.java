package com.example.raceapp.dto;

import java.util.List;
import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Detailed DTO for pilot responses with associated cars.
 */
@Getter
@Setter
public class PilotResponse {
    private Long id;
    private String name;
    private Integer age;
    private Integer experience;
    private List<CarSimpleResponse> cars;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PilotResponse that = (PilotResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}