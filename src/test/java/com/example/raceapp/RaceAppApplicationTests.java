package com.example.raceapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RaceAppApplicationTest {

    @Test
    void contextLoads() {
        // This test will verify that the Spring context loads successfully
    }

    @Test
    void main_ShouldStartApplication() {
        RaceAppApplication.main(new String[]{}); // This test verifies that the main method can be invoked
    }
}