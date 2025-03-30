package com.example.raceapp.controller;

import com.example.raceapp.exception.BadRequestException;
import com.example.raceapp.exception.GlobalExceptionHandler;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.service.LogService;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LogControllerTest {

    private MockMvc mockMvc;

    @Mock
    private LogService logService;

    @InjectMocks
    private LogController logController;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(logController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void downloadLogFile_Success() throws Exception {
        // Arrange
        String testDate = "01.01.2023";
        Resource mockResource = new ByteArrayResource("test content".getBytes());

        when(logService.getLogFileForDate(testDate)).thenReturn(mockResource);
        when(logService.parseDate(testDate)).thenReturn(LocalDate.of(2023, 1, 1));

        // Act & Assert
        mockMvc.perform(get("/api/logs/download")
                        .param("date", testDate))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"01.01.2023.log\""))
                .andExpect(content().bytes("test content".getBytes()));
    }

    @Test
    void downloadLogFile_InvalidDateFormat() throws Exception {
        // Arrange
        String invalidDate = "2023-01-01";
        when(logService.parseDate(invalidDate))
                .thenThrow(new BadRequestException("Invalid date format"));

        // Act & Assert
        mockMvc.perform(get("/api/logs/download")
                        .param("date", invalidDate))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Invalid date format"));
    }

    @Test
    void downloadLogFile_LogsNotFound() throws Exception {
        // Arrange
        String testDate = "02.02.2023";
        when(logService.getLogFileForDate(testDate))
                .thenThrow(new NotFoundException("Logs not found"));

        // Act & Assert
        mockMvc.perform(get("/api/logs/download")
                        .param("date", testDate))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Logs not found"));
    }

    @Test
    void downloadLogFile_InternalServerError() throws Exception {
        // Arrange
        String testDate = "03.03.2023";
        when(logService.getLogFileForDate(testDate))
                .thenThrow(new RuntimeException("Test error"));

        // Act & Assert
        mockMvc.perform(get("/api/logs/download")
                        .param("date", testDate))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Unexpected error: Test error"));
    }

    @Test
    void downloadLogFile_MissingParameter() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/logs/download"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Required parameter 'date' is not present"));
    }
}