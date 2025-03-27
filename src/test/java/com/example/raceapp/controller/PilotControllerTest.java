package com.example.raceapp.controller;

import com.example.raceapp.dto.PilotBulkRequest;
import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.service.PilotService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PilotController.class)
class PilotControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PilotService pilotService;

    @Test
    void createPilotsBulk_ValidRequest_Returns201() throws Exception {
        PilotResponse response = new PilotResponse();
        response.setId(1L);
        when(pilotService.createPilotsBulk(anyList())).thenReturn(List.of(response));

        String requestBody = """
            {
                "pilots": [
                    { "name": "Lewis Hamilton", "age": 38, "experience": 15 }
                ]
            }
            """;

        mockMvc.perform(post("/pilots/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    void createPilotsBulk_InvalidRequest_Returns400() throws Exception {
        String invalidRequestBody = """
            {
                "pilots": [
                    { "name": "", "age": 17, "experience": -1 }
                ]
            }
            """;

        mockMvc.perform(post("/pilots/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest());
    }
}