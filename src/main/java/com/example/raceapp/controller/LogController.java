package com.example.raceapp.controller;

import com.example.raceapp.exception.CustomException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    @GetMapping
    public ResponseEntity<String> getLogs(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        String logPath = "logs/application-" + date + ".log";
        try {
            String content = new String(Files.readAllBytes(Paths.get(logPath)));
            return ResponseEntity.ok(content);
        } catch (IOException e) {
            throw new CustomException(HttpStatus.NOT_FOUND, "Log file not found");
        }
    }

}