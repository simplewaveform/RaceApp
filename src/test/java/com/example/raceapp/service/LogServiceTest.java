package com.example.raceapp.service;

import com.example.raceapp.exception.BadRequestException;
import com.example.raceapp.exception.InternalServerException;
import com.example.raceapp.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @InjectMocks
    private LogService logService;

    @Test
    void parseDate_ValidFormat_ReturnsLocalDate() {
        String date = "01.01.2023";
        LocalDate result = logService.parseDate(date);
        assertEquals(LocalDate.of(2023, 1, 1), result);
    }

    @Test
    void parseDate_InvalidFormat_ThrowsException() {
        String date = "2023-01-01";
        assertThrows(BadRequestException.class, () -> logService.parseDate(date));
    }

    @Test
    void getLogFileForDate_ValidDate_ReturnsResource() {
        String date = "01.01.2023";
        String logLine = "2023-01-01 INFO Some log message";

        try (var mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.lines(Paths.get("logs/application.log")))
                    .thenReturn(java.util.stream.Stream.of(logLine));

            Resource result = logService.getLogFileForDate(date);
            assertNotNull(result);
        }
    }

    @Test
    void getLogFileForDate_NoLogsForDate_ThrowsException() {
        String date = "01.01.2023";

        try (var mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.lines(Paths.get("logs/application.log")))
                    .thenReturn(java.util.stream.Stream.empty());

            assertThrows(NotFoundException.class, () -> logService.getLogFileForDate(date));
        }
    }

    @Test
    void getLogFileForDate_IOException_ThrowsInternalServerException() {
        String date = "01.01.2023";

        try (var mockedFiles = mockStatic(Files.class)) {
            mockedFiles.when(() -> Files.lines(Paths.get("logs/application.log")))
                    .thenThrow(new IOException("File not found"));

            assertThrows(InternalServerException.class, () -> logService.getLogFileForDate(date));
        }
    }

    @Test
    void getLogFileForDate_NullInput_ThrowsBadRequest() {
        assertThrows(BadRequestException.class, () ->
                logService.getLogFileForDate(null));
    }

    @Test
    void getLogFileForDate_EmptyInput_ThrowsBadRequest() {
        assertThrows(BadRequestException.class, () ->
                logService.getLogFileForDate(""));
    }

    @Test
    void getLogFileForDate_BlankInput_ThrowsBadRequest() {
        assertThrows(BadRequestException.class, () ->
                logService.getLogFileForDate("   "));
    }
}