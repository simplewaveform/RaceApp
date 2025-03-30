package com.example.raceapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class PilotBulkRequest {
    @Valid
    @NotEmpty(message = "Pilots list cannot be empty")
    private List<@Valid PilotDto> pilots;

    public List<PilotDto> getPilots() { return pilots; }
    public void setPilots(List<PilotDto> pilots) { this.pilots = pilots; }
}