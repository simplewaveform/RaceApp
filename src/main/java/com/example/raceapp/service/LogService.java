package com.example.raceapp.service;

import com.example.raceapp.exception.BadRequestException;
import com.example.raceapp.exception.InternalServerException;
import com.example.raceapp.exception.NotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for handling operations related to log files.
 * This service provides functionality to retrieve log entries for a specific date.
 */
@Service
public class LogService {
    private static final String LOG_FILE_PATH = "logs/application.log";
    public static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd.MM.yyyy");

    /**
     * Retrieves the log file content for a specific date.
     *
     * @param date The date for which to retrieve the logs in the format "dd.MM.yyyy".
     * @return A Resource containing the log entries for the specified date.
     * @throws NotFoundException with HttpStatus.NOT_FOUND if no logs are found for the given date.
     * @throws InternalServerException with HttpStatus.INTERNAL_SERVER_ERROR if there is an error
     *         reading the log file.
     * @throws BadRequestException with HttpStatus.BAD_REQUEST if the provided date
     *         format is invalid.
     */
    public Resource getLogFileForDate(String date) {

        if (date == null || date.isEmpty()) {
            throw new BadRequestException("Date cannot be null or empty");
        }
        LocalDate targetDate = parseDate(date);

        try (Stream<String> stream = Files.lines(Paths.get(LOG_FILE_PATH))) {
            List<String> filteredLines = stream
                    .filter(line -> line.startsWith(targetDate.toString()))
                    .toList();

            if (filteredLines.isEmpty()) {
                throw new NotFoundException("No logs found for date: " + date);
            }

            return new ByteArrayResource(
                    String.join("\n", filteredLines).getBytes(StandardCharsets.UTF_8)
            );

        } catch (IOException e) {
            throw new InternalServerException("Error reading log file: " + e.getMessage());
        }
    }

    /**
     * Parses a date string into a LocalDate object using the specified format.
     *
     * @param date The date string to be parsed in the format "dd.MM.yyyy".
     * @return A LocalDate object representing the parsed date.
     * @throws BadRequestException with HttpStatus.BAD_REQUEST if the provided date
     *         format is invalid.
     */
    public LocalDate parseDate(String date) {
        try {
            return LocalDate.parse(date, DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format. Use dd.MM.yyyy");
        }
    }

}