package com.example.raceapp.controller;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.service.CarService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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


    @Test
    void getCarById_NonExisting_ReturnsNotFound() throws Exception {
        when(carService.getCarById(anyLong())).thenReturn(Optional.empty());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders.get("/cars/999");

        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }

    @Test
    public void getCars_FilteredParameters_ReturnsFilteredResults() throws Exception {
        // Arrange
        String brand = "Red Bull";
        String model = "RB25";
        Integer power = 980;
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        CarResponse carResponse = new CarResponse();
        carResponse.setId(1L);
        Page<CarResponse> mockPage = new PageImpl<>(List.of(carResponse));

        when(carService.searchCarsWithPagination(brand, model, power, ownerId, pageable))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/cars")
                        .param("brand", brand)
                        .param("model", model)
                        .param("power", String.valueOf(power))
                        .param("ownerId", String.valueOf(ownerId))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(carService).searchCarsWithPagination(brand, model, power, ownerId, pageable);
    }

    @Test
    public void getCars_NoFilters_ReturnsAll() throws Exception {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<CarResponse> mockPage = new PageImpl<>(List.of());

        when(carService.searchCarsWithPagination(null, null, null, null, pageable))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/cars")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(carService).searchCarsWithPagination(null, null, null, null, pageable);
    }

    @Test
    public void getCarsByPower_ValidPower_ReturnsCars() throws Exception {
        // Arrange
        Integer minPower = 900;
        Pageable pageable = PageRequest.of(0, 10);

        CarResponse carResponse = new CarResponse();
        carResponse.setId(1L);
        Page<CarResponse> mockPage = new PageImpl<>(List.of(carResponse));

        when(carService.getCarsByPower(minPower, pageable))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/cars/by-power")
                        .param("minPower", String.valueOf(minPower))
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1));

        verify(carService).getCarsByPower(minPower, pageable);
    }

    @Test
    public void deleteCar_ExistingId_ReturnsNoContent() throws Exception {
        // Arrange
        Long existingId = 1L;

        doNothing().when(carService).deleteCar(existingId);

        // Act & Assert
        mockMvc.perform(delete("/cars/{id}", existingId))
                .andExpect(status().isNoContent());

        verify(carService).deleteCar(existingId);
    }

    @Test
    public void deleteCar_NonExistingId_Returns404() throws Exception {
        // Arrange
        Long nonExistingId = 999L;

        doThrow(new NotFoundException("Car not found")).when(carService).deleteCar(nonExistingId);

        // Act & Assert
        mockMvc.perform(delete("/cars/{id}", nonExistingId))
                .andExpect(status().isNotFound());

        verify(carService).deleteCar(nonExistingId);
    }

    @Test
    public void updateCar_ValidRequest_Returns200() throws Exception {
        // Arrange
        Long carId = 1L;
        CarDto carDto = new CarDto();
        carDto.setBrand("Mercedes");
        carDto.setModel("W14");
        carDto.setPower(1000);
        carDto.setOwnerId(2L);

        CarResponse updatedResponse = new CarResponse();
        updatedResponse.setId(carId);
        updatedResponse.setBrand("Mercedes");
        updatedResponse.setModel("W14");
        updatedResponse.setPower(1000);

        when(carService.updateCar(eq(carId), any(CarDto.class))).thenReturn(Optional.of(updatedResponse));

        // Act & Assert
        mockMvc.perform(put("/cars/{id}", carId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(carDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carId))
                .andExpect(jsonPath("$.brand").value("Mercedes"))
                .andExpect(jsonPath("$.model").value("W14"))
                .andExpect(jsonPath("$.power").value(1000));

        verify(carService).updateCar(eq(carId), any(CarDto.class));
    }

    @Test
    void updateCar_InvalidId_ReturnsNotFound() throws Exception {
        CarDto request = new CarDto();
        request.setModel("Invalid Car");
        request.setBrand("W14");
        request.setPower(150);
        request.setOwnerId(1L);

        when(carService.updateCar(eq(999L), any(CarDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/cars/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound());
    }


}