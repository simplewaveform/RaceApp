package com.example.raceapp.dto;

import jakarta.validation.Valid;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PilotBulkRequest {
    @Valid
    private List<PilotDto> pilots;
}