package com.example.raceapp.controller;

import com.example.raceapp.dto.PilotBulkRequest;
import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.service.PilotService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PilotController.class)
@Import(PilotControllerTest.TestConfig.class)
public class PilotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PilotService pilotService;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public PilotService pilotService() {
            return Mockito.mock(PilotService.class);
        }
    }

    @Test
    public void createPilotsBulk_ValidRequest_Returns201() throws Exception {
        PilotDto validPilot = new PilotDto();
        validPilot.setName("Valid Pilot");
        validPilot.setAge(25); // Set required fields
        validPilot.setExperience(3);

        PilotBulkRequest request = new PilotBulkRequest();
        request.setPilots(List.of(validPilot)); // Valid data

        when(pilotService.createPilotsBulk(any())).thenReturn(List.of(new PilotResponse()));

        mockMvc.perform(post("/pilots/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    public void createPilotsBulk_InvalidRequest_Returns400() throws Exception {
        PilotDto invalidPilot = new PilotDto(); // Missing required fields
        PilotBulkRequest invalidRequest = new PilotBulkRequest();
        invalidRequest.setPilots(List.of(invalidPilot)); // Invalid data

        mockMvc.perform(post("/pilots/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void createPilot_ValidRequest_Returns201() throws Exception {
        PilotDto pilotDto = new PilotDto();
        pilotDto.setName("New Pilot");
        pilotDto.setAge(25);
        pilotDto.setExperience(3);

        PilotResponse pilotResponse = new PilotResponse();
        pilotResponse.setId(1L);
        pilotResponse.setName("New Pilot");
        pilotResponse.setAge(25);
        pilotResponse.setExperience(3);

        when(pilotService.createPilot(any(PilotDto.class))).thenReturn(pilotResponse);

        mockMvc.perform(post("/pilots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(pilotDto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void createPilot_InvalidRequest_Returns400() throws Exception {
        PilotDto invalidPilot = new PilotDto(); // Missing required fields

        mockMvc.perform(post("/pilots")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidPilot)))
                .andExpect(status().isBadRequest());
    }


}