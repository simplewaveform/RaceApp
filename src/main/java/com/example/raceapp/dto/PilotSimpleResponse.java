package com.example.raceapp.dto;

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
}