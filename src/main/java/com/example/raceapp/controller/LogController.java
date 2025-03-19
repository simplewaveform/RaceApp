package com.example.raceapp.controller;

import com.example.raceapp.exception.CustomException;
import com.example.raceapp.service.LogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
public class LogController {

    private final LogService logService;

    /**
     * Constructs a new LogController with the provided LogService.
     *
     * @param logService The LogService used to retrieve log files.
     */
    public LogController(LogService logService) {
        this.logService = logService;
    }

    /**
     * Endpoint to download log file for a specific date.
     *
     * @param date The date for which to retrieve the logs in the format "dd.MM.yyyy".
     * @return A ResponseEntity containing the log file as a Resource.
     * @throws CustomException with HttpStatus.NOT_FOUND if no logs are found for the given date.
     * @throws CustomException with HttpStatus.INTERNAL_SERVER_ERROR if there is an
     *         error reading the log file.
     * @throws CustomException with HttpStatus.BAD_REQUEST if the provided date format is invalid.
     */
    @GetMapping("/download")
    @Operation(summary = "Download log file for a specific date")
    public ResponseEntity<Resource> downloadLogFile(@RequestParam String date) {
        Resource resource = logService.getLogFileForDate(date);
        String downloadFileName = logService.parseDate(date).format(LogService.DATE_FORMATTER)
                + ".log";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""
                        + downloadFileName + "\"")
                .body(resource);
    }
}