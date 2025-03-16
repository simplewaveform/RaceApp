package com.example.raceapp.dto;

import java.util.Objects;
import lombok.Getter;
import lombok.Setter;

/**
 * Simplified DTO for pilot references in other responses.
 */
@Getter
@Setter
public class PilotSimpleResponse {
    private Long id;
    private String name;
    private Integer experience;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PilotSimpleResponse that = (PilotSimpleResponse) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}