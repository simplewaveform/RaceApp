package com.example.raceapp.controller;

import com.example.raceapp.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Value("${logging.file.path:logs/}")
    private String logDir;

    private static final int LINES_PER_PAGE = 100;

    @Operation(
            summary = "Get log file by date",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Log content retrieved"),
                    @ApiResponse(responseCode = "404", description = "Log file not found")
            }
    )
    @GetMapping
    public ResponseEntity<List<String>> getLogs(
            @Parameter(description = "Date in format YYYY-MM-DD")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page) {

        validateDate(date);
        Path logPath = Paths.get(logDir + "application-" + date + ".log");
        validateLogFile(logPath, date); // Передача date в метод

        try (Stream<String> lines = Files.lines(logPath)) {
            List<String> logLines = lines
                    .skip((long) page * LINES_PER_PAGE)
                    .limit(LINES_PER_PAGE)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(logLines);
        } catch (IOException e) {
            throw new CustomException(HttpStatus.INTERNAL_SERVER_ERROR, "Error reading log file");
        }
    }

    private void validateLogFile(Path logPath, LocalDate date) {
        if (!Files.exists(logPath)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Log file not found for date: " + date);
        }
    }

    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Future date is invalid");
        }
    }
}