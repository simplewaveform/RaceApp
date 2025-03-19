package com.example.raceapp.controller;

import com.example.raceapp.exception.CustomException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for log handling.
 */
@RestController
@RequestMapping("/api/logs")
public class LogController {

    @Value("${logging.file.path:logs/}")
    private String logDir;

    private static final int LINES_PER_PAGE = 100;

    /**
     * Retrieves log file content for a specific date with pagination support.
     *
     * @param date the date of the log file in YYYY-MM-DD format
     * @param page the page number (0-based index) for pagination
     * @return ResponseEntity with a list of log entries
     */
    @Operation(
            summary = "Get log file by date",
            description = "Retrieves log content for a specific date with pagination support.",
            responses = {
                @ApiResponse(responseCode = "200", description = "Log content retrieved"),
                @ApiResponse(responseCode = "404", description = "Log file not found"),
                @ApiResponse(responseCode = "400", description = "Invalid date or pagination "
                        + "parameters"),
                @ApiResponse(responseCode = "500", description = "Error reading log file")
            }
    )
    @GetMapping
    public ResponseEntity<List<String>> getLogs(
            @Parameter(description = "Date in format YYYY-MM-DD")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page) {

        validateDate(date);
        Path logPath = Paths.get(logDir + "application-" + date + ".log");
        validateLogFile(logPath, date);

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

    /**
     * Validates that the log file exists for the given date.
     *
     * @param logPath the path of the log file
     * @param date the date of the log file
     * @throws CustomException if the log file does not exist
     */
    private void validateLogFile(Path logPath, LocalDate date) {
        if (!Files.exists(logPath)) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Log file not found for date: " + date);
        }
    }

    /**
     * Validates that the provided date is not in the future.
     *
     * @param date the date to validate
     * @throws CustomException if the date is in the future
     */
    private void validateDate(LocalDate date) {
        if (date.isAfter(LocalDate.now())) {
            throw new CustomException(HttpStatus.BAD_REQUEST, "Future date is invalid");
        }
    }
}
