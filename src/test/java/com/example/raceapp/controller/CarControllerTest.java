// CarControllerTest.java
package com.example.raceapp.controller;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public CarService carService() {
            return Mockito.mock(CarService.class);
        }
    }

    @Autowired
    private CarService carService;

    @Test
    public void createCar_ValidRequest_Returns201() throws Exception {
        CarDto carDto = new CarDto();
        carDto.setBrand("Red Bull");
        carDto.setModel("RB25");
        carDto.setPower(980);
        carDto.setOwnerId(1L);

        CarResponse response = new CarResponse();
        response.setId(1L);
        response.setBrand("Red Bull");
        response.setModel("RB25");
        response.setPower(980);

        when(carService.createCar(any(CarDto.class))).thenReturn(response);

        mockMvc.perform(post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.brand").value("Red Bull"));
    }

    @Test
    public void getCarById_ExistingId_Returns200() throws Exception {
        Long carId = 1L;
        CarResponse response = new CarResponse();
        response.setId(carId);
        response.setBrand("Red Bull");

        when(carService.getCarById(carId)).thenReturn(Optional.of(response));

        mockMvc.perform(get("/cars/{id}", carId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carId));
    }

    @Test
    public void getCarById_NonExistingId_Returns404() throws Exception {
        Long nonExistingId = 999L;
        when(carService.getCarById(nonExistingId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/cars/{id}", nonExistingId))
                .andExpect(status().isNotFound());
    }
}