package com.example.raceapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

/**
 * The main class to run the Spring Boot application for the RaceApp.
 */
@EnableSpringDataWebSupport
@SpringBootApplication
public class RaceAppApplication {

    /**
     * The entry point of the Spring Boot application.
     *
     * @param args command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        SpringApplication.run(RaceAppApplication.class, args);
    }
}
