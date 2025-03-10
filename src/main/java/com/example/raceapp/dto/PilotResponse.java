package com.example.raceapp.dto;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

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
}