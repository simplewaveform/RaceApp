package com.example.raceapp.controller;

import com.example.raceapp.exception.ApiException;
import com.example.raceapp.exception.BadRequestException;
import com.example.raceapp.exception.InternalServerException;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.service.LogService;
import com.example.raceapp.service.LogTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Controller for downloading logs in the Race App.
 */
@RestController
@RequestMapping("/api/logs")
@Tag(name = "Log API", description = "Get API Logs")
public class LogController {

    private final LogService logService;
    private final LogTaskService logTaskService;

    @Autowired
    public LogController(LogService logService, LogTaskService logTaskService) {
        this.logService = logService;
        this.logTaskService = logTaskService;
    }

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

    @PostMapping("/generate")
    @Operation(summary = "Start log generation")
    public ResponseEntity<Map<String, String>> generateLog(
            @Parameter(description = "Date in dd.MM.yyyy format")
            @RequestParam String date) {

        String taskId = logTaskService.startLogGeneration(date);
        return ResponseEntity.ok(Collections.singletonMap(
                "taskId", taskId
        ));
    }

    @GetMapping("/status/{taskId}")
    @Operation(summary = "Get task status")
    public ResponseEntity<Map<String, String>> getStatus(
            @Parameter(description = "8-char task ID")
            @PathVariable String taskId) {

        LogTaskService.TaskWrapper wrapper = logTaskService.getTasks().get(taskId);
        if (wrapper == null) throw new NotFoundException("Task not found");

        if (wrapper.future.isCompletedExceptionally()) {
            try {
                wrapper.future.get();
            } catch (InterruptedException | ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof ApiException) {
                    throw (ApiException) cause;
                }
                throw new InternalServerException("Task failed: " + e.getMessage());
            }
        }
        String status = logTaskService.getTaskStatus(taskId);
        return ResponseEntity.ok(Collections.singletonMap(
                "status", status
        ));
    }

    @GetMapping("/result/{taskId}")
    @Operation(summary = "Get generated log")
    public ResponseEntity<Resource> getResult(
            @Parameter(description = "8-char task ID")
            @PathVariable String taskId) {

        try {
            Resource resource = logTaskService.getTaskResult(taskId);
            String filename = "logs_" + Instant.now().toString() + ".log";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + filename + "\"")
                    .body(resource);

        } catch (IllegalStateException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

}
