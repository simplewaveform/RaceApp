package com.example.raceapp.controller;

import com.example.raceapp.exception.BadRequestException;
import com.example.raceapp.exception.InternalServerException;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
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
 * Controller for downloading logs in the Race App.
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log API", description = "Get API Logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    /**
     * Downloader of log file.
     *
     * @param date requested date of logs.
     * @return log file.
     */
    @Operation(summary = "Download log file", responses = {
        @ApiResponse(responseCode = "200", description = "Log file retrieved",
                    content = @Content(schema = @Schema(type = "string", format = "binary"))),
        @ApiResponse(responseCode = "400", description = "Invalid date format",
                    content = @Content(schema = @Schema(example = "{ \"error\":"
                            + "\"Invalid date format\" }"))),
        @ApiResponse(responseCode = "404", description = "Logs not found for the specified date",
                    content = @Content(schema = @Schema(example = "{ \"error\":"
                            + "\"Logs not found\" }"))),
        @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(schema = @Schema(example = "{ \"error\":"
                            + "\"Internal server error\" }")))
    })
    @GetMapping("/download")
    public ResponseEntity<Resource> downloadLogFile(
            @Parameter(description = "Date in dd.MM.yyyy format", required = true,
                    example = "01.01.2023")
            @RequestParam String date) {

        try {
            Resource resource = logService.getLogFileForDate(date);
            String fileName = logService.parseDate(date).format(LogService.DATE_FORMATTER) + ".log";

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                            + fileName + "\"")
                    .body(resource);

        } catch (BadRequestException | NotFoundException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new InternalServerException("Unexpected error: " + e.getMessage());
        }
    }
}
