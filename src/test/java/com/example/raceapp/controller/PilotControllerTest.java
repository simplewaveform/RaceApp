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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.example.raceapp.exception.NotFoundException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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

    @Test
    void updatePilot_ValidRequest_ReturnsOk() throws Exception {
        PilotDto request = new PilotDto();
        request.setName("Updated Pilot");
        request.setAge(30);
        request.setExperience(5);

        PilotResponse response = new PilotResponse();
        response.setId(1L);
        response.setName("Updated Pilot");
        response.setAge(30);
        response.setExperience(5);

        when(pilotService.updatePilot(anyLong(), any())).thenReturn(Optional.of(response));

        mockMvc.perform(put("/pilots/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void updatePilot_InvalidId_ReturnsNotFound() throws Exception {
        PilotDto request = new PilotDto();
        request.setName("Invalid Pilot");
        request.setAge(30);
        request.setExperience(5);

        when(pilotService.updatePilot(eq(999L), any(PilotDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/pilots/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    void getPilotById_ValidId_ReturnsOk() throws Exception {
        PilotResponse response = new PilotResponse();
        response.setId(1L);
        when(pilotService.getPilotById(anyLong())).thenReturn(Optional.of(response));

        mockMvc.perform(get("/pilots/1"))
                .andExpect(status().isOk());
    }

    @Test
    void getPilotById_InvalidId_ReturnsNotFound() throws Exception {
        when(pilotService.getPilotById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/pilots/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void deletePilot_ValidId_ReturnsNoContent() throws Exception {
        doNothing().when(pilotService).deletePilot(anyLong());

        mockMvc.perform(delete("/pilots/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deletePilot_InvalidId_ThrowsException() throws Exception {
        doThrow(new NotFoundException("Pilot not found")).when(pilotService).deletePilot(anyLong());

        mockMvc.perform(delete("/pilots/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createPilotsBulk_ValidRequest_Returns201() throws Exception {
        // Given: Валидный запрос с двумя пилотами
        PilotDto pilot1 = new PilotDto();
        pilot1.setName("Max Verstappen");
        pilot1.setAge(30);
        pilot1.setExperience(5);

        PilotDto pilot2 = new PilotDto();
        pilot2.setName("Lewis Hamilton");
        pilot2.setAge(38);
        pilot2.setExperience(15);

        PilotBulkRequest validRequest = new PilotBulkRequest();
        validRequest.setPilots(List.of(pilot1, pilot2));

        // Mock: Сервис возвращает созданные объекты
        PilotResponse response1 = new PilotResponse();
        response1.setId(1L);
        response1.setName("Max Verstappen");
        response1.setAge(30);
        response1.setExperience(5);

        PilotResponse response2 = new PilotResponse();
        response2.setId(2L);
        response2.setName("Lewis Hamilton");
        response2.setAge(38);
        response2.setExperience(15);

        when(pilotService.createPilotsBulk(anyList()))
                .thenReturn(List.of(response1, response2));

        // When/Then: Отправка запроса и проверки
        mockMvc.perform(post("/pilots/bulk")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Max Verstappen"))
                .andExpect(jsonPath("$[1].age").value(38))
                .andExpect(jsonPath("$[1].experience").value(15));
    }

    @Test
    public void getPilots_WithFilters_ReturnsFilteredResults() throws Exception {
        // Arrange
        String name = "Max";
        Integer age = 30;
        Integer experience = 5;
        Pageable pageable = PageRequest.of(0, 10);

        // Initialize PilotResponse with all required fields
        PilotResponse pilotResponse = new PilotResponse();
        pilotResponse.setId(1L);
        pilotResponse.setName(name); // Set the name to match the filter
        pilotResponse.setAge(age);
        pilotResponse.setExperience(experience);

        Page<PilotResponse> mockPage = new PageImpl<>(List.of(pilotResponse));

        when(pilotService.searchPilotsWithPagination(name, age, experience, pageable))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/pilots")
                        .param("name", name)
                        .param("age", String.valueOf(age))
                        .param("experience", String.valueOf(experience))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value(name)); // Now matches "Max"

        verify(pilotService).searchPilotsWithPagination(name, age, experience, pageable);
    }

    @Test
    public void getPilotsByCarBrand_ValidBrand_ReturnsPilots() throws Exception {
        // Arrange
        String brand = "Red Bull";
        Pageable pageable = PageRequest.of(0, 10);

        PilotResponse pilotResponse = new PilotResponse();
        pilotResponse.setId(1L);
        pilotResponse.setName("Max Verstappen");

        Page<PilotResponse> mockPage = new PageImpl<>(List.of(pilotResponse));

        when(pilotService.getPilotsByCarBrandNative(brand, pageable))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/pilots/by-car-brand")
                        .param("brand", brand)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Max Verstappen"));

        verify(pilotService).getPilotsByCarBrandNative(brand, pageable);
    }

    @Test
    public void getPilotsByCarBrand_InvalidBrand_ReturnsEmpty() throws Exception {
        // Arrange
        String brand = "NonExistentBrand";
        Pageable pageable = PageRequest.of(0, 10);

        Page<PilotResponse> mockPage = new PageImpl<>(List.of());

        when(pilotService.getPilotsByCarBrandNative(brand, pageable))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/pilots/by-car-brand")
                        .param("brand", brand)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty());

        verify(pilotService).getPilotsByCarBrandNative(brand, pageable);
    }

    @Test
    public void getPilotsByCarBrand_MissingBrand_Returns400() throws Exception {
        mockMvc.perform(get("/pilots/by-car-brand")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void searchPilots_withExperienceNull_returnsResults() throws Exception {
        mockMvc.perform(get("/pilots")
                        .param("experience", "") // Явно передаем пустое значение
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());
    }

}