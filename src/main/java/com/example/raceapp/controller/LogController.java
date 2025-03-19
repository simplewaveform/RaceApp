package com.example.raceapp.controller;

import com.example.raceapp.exception.BadRequestException;
import com.example.raceapp.exception.InternalServerException;
import com.example.raceapp.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling log-related operations.
 * This controller provides endpoints to download log files for a specific date.
 */
@RestController
@RequestMapping("api/logs")
@Tag(name = "Log API", description = "Get API Logs")
@RequiredArgsConstructor
public class LogController {
    private final LogService logService;

    /**
     * Endpoint to download log file for a specific date.
     *
     * @param date The date for which to retrieve the logs in the format "dd.MM.yyyy".
     * @return A ResponseEntity containing the log file as a Resource.
     */
    @Operation(
            summary = "Download log file",
            description = "Downloads log file for specified date",
            responses = {
                @ApiResponse(responseCode = "200", description = "Log file retrieved",
                            content = @Content(schema = @Schema(type = "string",
                                    format = "binary"))),
                @ApiResponse(responseCode = "400", description = "Invalid date format"),
                @ApiResponse(responseCode = "404", description = "Logs not found for date"),
                @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadLogFile(
            @Parameter(description = "Date in dd.MM.yyyy format",
                    required = true, example = "01.01.2023")
            @RequestParam String date) {
        try {
            Resource resource = logService.getLogFileForDate(date);
            String fileName = logService.parseDate(date).format(LogService.DATE_FORMATTER) + ".log";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                            + fileName + "\"")
                    .body(resource);

        } catch (IllegalArgumentException e) {
            throw new BadRequestException(e.getMessage());
        } catch (RuntimeException e) {
            throw new InternalServerException("Error processing log file: " + e.getMessage());
        }
    }
}