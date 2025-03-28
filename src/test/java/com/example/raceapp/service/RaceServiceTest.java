package com.example.raceapp.service;

import com.example.raceapp.dto.*;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.model.*;
import com.example.raceapp.repository.RaceRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RaceServiceTest {

    @Mock
    private RaceRepository raceRepository;

    @Mock
    private PilotService pilotService;

    @Mock
    private CarService carService;

    @InjectMocks
    private RaceService raceService;

    @Test
    void createRace_ValidInput_ReturnsRaceResponse() {
        // Given
        RaceDto dto = new RaceDto();
        dto.setName("Monaco Grand Prix");
        dto.setYear(2023);
        dto.setPilotIds(Set.of(1L, 2L));
        dto.setCarIds(Set.of(1L, 2L));

        Pilot pilot1 = new Pilot();
        pilot1.setId(1L);
        pilot1.setName("Lewis Hamilton");
        Pilot pilot2 = new Pilot();
        pilot2.setId(2L);
        pilot2.setName("Max Verstappen");

        Car car1 = new Car();
        car1.setId(1L);
        car1.setBrand("Mercedes");
        Car car2 = new Car();
        car2.setId(2L);
        car2.setBrand("Red Bull");

        // Мокируем вызовы зависимостей
        when(pilotService.getPilotsByIds(dto.getPilotIds())).thenReturn(Set.of(pilot1, pilot2));
        when(carService.getCarsByIds(dto.getCarIds())).thenReturn(Set.of(car1, car2));
        when(pilotService.mapToResponse(any(Pilot.class))).thenAnswer(inv -> {
            Pilot p = inv.getArgument(0);
            PilotResponse response = new PilotResponse();
            response.setId(p.getId());
            response.setName(p.getName());
            return response;
        });
        when(carService.mapToResponse(any(Car.class))).thenAnswer(inv -> {
            Car c = inv.getArgument(0);
            CarResponse response = new CarResponse();
            response.setId(c.getId());
            response.setBrand(c.getBrand());
            return response;
        });
        when(raceRepository.save(any(Race.class))).thenAnswer(inv -> {
            Race race = inv.getArgument(0);
            race.setId(1L);
            return race;
        });

        // When
        RaceResponse result = raceService.createRace(dto);

        // Then
        assertNotNull(result);
        assertEquals("Monaco Grand Prix", result.getName());
        assertEquals(2023, result.getYear());
        assertEquals(2, result.getPilots().size());
        assertEquals(2, result.getCars().size());

        // Проверяем, что сервисы были вызваны с правильными параметрами
        verify(pilotService).getPilotsByIds(dto.getPilotIds());
        verify(carService).getCarsByIds(dto.getCarIds());
        verify(raceRepository).save(any(Race.class));
    }

    @Test
    void getRaceById_ExistingId_ReturnsRace() {
        // Given
        Long raceId = 1L;
        Race race = new Race();
        race.setId(raceId);
        race.setName("Spanish Grand Prix");
        race.setYear(2023);

        when(raceRepository.findById(raceId)).thenReturn(Optional.of(race));

        // When
        Optional<RaceResponse> result = raceService.getRaceById(raceId);

        // Then
        assertTrue(result.isPresent());
        assertEquals(raceId, result.get().getId());
        assertEquals("Spanish Grand Prix", result.get().getName());

        // Проверяем, что репозиторий был вызван
        verify(raceRepository).findById(raceId);
    }

    @Test
    void updateRace_ValidInput_UpdatesRace() {
        // Given
        Long raceId = 1L;
        RaceDto dto = new RaceDto();
        dto.setName("Updated Race");
        dto.setYear(2024);
        dto.setPilotIds(Set.of(3L));
        dto.setCarIds(Set.of(3L));

        Race existingRace = new Race();
        existingRace.setId(raceId);
        existingRace.setName("Old Race");
        existingRace.setYear(2023);

        Pilot newPilot = new Pilot();
        newPilot.setId(3L);
        Car newCar = new Car();
        newCar.setId(3L);

        when(raceRepository.findById(raceId)).thenReturn(Optional.of(existingRace));
        when(pilotService.getPilotsByIds(dto.getPilotIds())).thenReturn(Set.of(newPilot));
        when(carService.getCarsByIds(dto.getCarIds())).thenReturn(Set.of(newCar));
        when(pilotService.mapToResponse(any(Pilot.class))).thenReturn(new PilotResponse());
        when(carService.mapToResponse(any(Car.class))).thenReturn(new CarResponse());
        when(raceRepository.save(any(Race.class))).thenReturn(existingRace);

        // When
        Optional<RaceResponse> result = raceService.updateRace(raceId, dto);

        // Then
        assertTrue(result.isPresent());
        assertEquals("Updated Race", result.get().getName());
        assertEquals(2024, result.get().getYear());
        assertEquals(1, result.get().getPilots().size());
        assertEquals(1, result.get().getCars().size());
    }

    @Test
    void deleteRace_ExistingId_DeletesRace() {
        // Given
        Long raceId = 1L;
        Race race = new Race();
        race.setId(raceId);

        when(raceRepository.findById(raceId)).thenReturn(Optional.of(race));

        // When
        raceService.deleteRace(raceId);

        // Then
        verify(raceRepository, times(1)).delete(race);
    }

    @Test
    void deleteRace_NonExistingId_ThrowsException() {
        // Given
        Long nonExistingId = 999L;
        when(raceRepository.findById(nonExistingId)).thenReturn(Optional.empty());

        // Then
        assertThrows(NotFoundException.class, () -> {
            raceService.deleteRace(nonExistingId);
        });
    }
}