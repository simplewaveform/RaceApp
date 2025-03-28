package com.example.raceapp.service;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.PilotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private PilotRepository pilotRepository;

    @InjectMocks
    private CarService carService;

    @Test
    void createCar_ValidInput_ReturnsCarResponse() {
        // Given
        CarDto dto = new CarDto();
        dto.setBrand("Ferrari");
        dto.setModel("SF-23");
        dto.setPower(950);
        dto.setOwnerId(1L);

        Pilot owner = new Pilot();
        owner.setId(1L);
        owner.setName("Charles Leclerc");

        when(pilotRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> {
            Car car = inv.getArgument(0);
            car.setId(1L);
            return car;
        });

        // When
        CarResponse result = carService.createCar(dto);

        // Then
        assertNotNull(result);
        assertEquals("Ferrari", result.getBrand());
        assertEquals(1L, result.getOwner().getId());
        verify(carRepository, times(1)).save(any(Car.class));
    }

    @Test
    void getCarById_ExistingId_ReturnsCar() {
        // Given
        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);
        car.setBrand("Red Bull");
        car.setModel("RB19");
        car.setPower(1000);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // When
        Optional<CarResponse> result = carService.getCarById(carId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(carId, result.get().getId());
        assertEquals("Red Bull", result.get().getBrand());
    }

    @Test
    void updateCar_ValidInput_UpdatesCar() {
        // Given
        Long carId = 1L;
        CarDto dto = new CarDto();
        dto.setBrand("Mercedes");
        dto.setModel("W14");
        dto.setPower(950);
        dto.setOwnerId(2L);

        Car existingCar = new Car();
        existingCar.setId(carId);
        existingCar.setBrand("Mercedes");
        existingCar.setModel("W13");
        existingCar.setPower(900);

        Pilot newOwner = new Pilot();
        newOwner.setId(2L);
        newOwner.setName("Lewis Hamilton");

        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(pilotRepository.findById(2L)).thenReturn(Optional.of(newOwner));
        when(carRepository.save(any(Car.class))).thenReturn(existingCar);

        // When
        Optional<CarResponse> result = carService.updateCar(carId, dto);

        // Then
        assertTrue(result.isPresent());
        assertEquals("W14", result.get().getModel());
        assertEquals(950, result.get().getPower());
        assertEquals(2L, result.get().getOwner().getId());
    }

    @Test
    void deleteCar_ExistingId_DeletesCar() {
        // Given
        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // When
        carService.deleteCar(carId);

        // Then
        verify(carRepository, times(1)).delete(car);
    }

    @Test
    void deleteCar_NonExistingId_ThrowsException() {
        // Given
        Long nonExistingId = 999L;
        when(carRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> {
            carService.deleteCar(nonExistingId);
        });
    }
}