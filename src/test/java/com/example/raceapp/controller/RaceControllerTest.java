package com.example.raceapp.controller;

import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.dto.RaceResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.service.RaceService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RaceController.class)
public class RaceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RaceService raceService() {
            return Mockito.mock(RaceService.class);
        }
    }

    @Autowired
    private RaceService raceService;

    private RaceDto createValidRaceDto() {
        RaceDto dto = new RaceDto();
        dto.setName("Grand Prix Miami");
        dto.setYear(2025);
        dto.setPilotIds(Set.of(1L, 2L));
        dto.setCarIds(Set.of(3L, 4L));
        return dto;
    }

    private RaceResponse createRaceResponse(Long id) {
        RaceResponse response = new RaceResponse();
        response.setId(id);
        response.setName("Grand Prix Miami");
        response.setYear(2025);
        return response;
    }

    @Test
    void createRace_ValidRequest_ReturnsCreated() throws Exception {
        // Arrange
        RaceDto request = createValidRaceDto();
        RaceResponse response = createRaceResponse(1L);

        when(raceService.createRace(any(RaceDto.class))).thenReturn(response);

        // Act & Assert
        mockMvc.perform(post("/races")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Grand Prix Miami"))
                .andExpect(jsonPath("$.year").value(2025));
    }

    @Test
    void createRace_InvalidRequest_ReturnsBadRequest() throws Exception {
        // Arrange
        RaceDto invalidRequest = new RaceDto(); // Empty request

        // Act & Assert
        mockMvc.perform(post("/races")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    // RaceControllerTest.java
    @Test
    void getAllRaces_ReturnsPaginatedResults() throws Exception {
        // Указываем параметры пагинации в запросе
        mockMvc.perform(get("/races")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        // Мокируем вызов сервиса с Pageable
        Pageable pageable = PageRequest.of(0, 10);
        when(raceService.getAllRaces(pageable)).thenReturn(new PageImpl<>(List.of()));
    }

    @Test
    void getRaceById_ExistingId_ReturnsRace() throws Exception {
        // Arrange
        Long raceId = 1L;
        RaceResponse response = createRaceResponse(raceId);
        when(raceService.getRaceById(raceId)).thenReturn(Optional.of(response));

        // Act & Assert
        mockMvc.perform(get("/races/{id}", raceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(raceId));
    }

    @Test
    void getRaceById_NonExistingId_ReturnsNotFound() throws Exception {
        // Arrange
        Long nonExistingId = 999L;
        when(raceService.getRaceById(nonExistingId)).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(get("/races/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateRace_ValidRequest_ReturnsUpdatedRace() throws Exception {
        Long raceId = 1L;

        // Создаем валидный DTO с обязательными полями
        RaceDto updateDto = new RaceDto();
        updateDto.setName("Updated Race");
        updateDto.setYear(2026);
        updateDto.setPilotIds(Set.of(1L, 2L)); // Обязательное поле
        updateDto.setCarIds(Set.of(3L, 4L));   // Обязательное поле

        // Создаем ожидаемый ответ
        RaceResponse updatedRace = new RaceResponse();
        updatedRace.setId(raceId);
        updatedRace.setName("Updated Race");
        updatedRace.setYear(2026);

        // Мокируем сервис
        when(raceService.updateRace(eq(raceId), any(RaceDto.class)))
                .thenReturn(Optional.of(updatedRace));

        // Отправляем запрос с полным телом, включая pilotIds и carIds
        mockMvc.perform(put("/races/{id}", raceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Updated Race\",\"year\":2026,\"pilotIds\":[1,2],\"carIds\":[3,4]}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Race"))
                .andExpect(jsonPath("$.year").value(2026));
    }

    @Test
    void updateRace_NonExistingId_ReturnsNotFound() throws Exception {
        // Arrange
        Long nonExistingId = 999L;
        when(raceService.updateRace(eq(nonExistingId), any(RaceDto.class))).thenReturn(Optional.empty());

        // Act & Assert
        mockMvc.perform(put("/races/{id}", nonExistingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createValidRaceDto())))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteRace_ExistingId_ReturnsNoContent() throws Exception {
        // Arrange
        Long raceId = 1L;
        when(raceService.getRaceById(raceId)).thenReturn(Optional.of(createRaceResponse(raceId)));

        // Act & Assert
        mockMvc.perform(delete("/races/{id}", raceId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteRace_NonExistingId_ReturnsNotFound() throws Exception {
        Long nonExistingId = 999L;

        // Мокируем исключение
        doThrow(new NotFoundException("Race not found"))
                .when(raceService).deleteRace(nonExistingId);

        mockMvc.perform(delete("/races/{id}", nonExistingId))
                .andExpect(status().isNotFound()); // Ожидаем 404
    }
}