package com.example.raceapp.service;

import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.dto.PilotSimpleResponse;
import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.dto.RaceResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.RaceRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        assertThrows(NotFoundException.class, () -> raceService.deleteRace(nonExistingId));
    }

    @Test
    void getAllRaces_ReturnsPage() {
        Pageable pageable = Pageable.unpaged();
        Race race = new Race();
        race.setId(1L);

        when(raceRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(race)));

        Page<RaceResponse> result = raceService.getAllRaces(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void mapToPilotSimpleResponse_ValidPilot_ReturnsResponse() {
        Pilot pilot = new Pilot();
        pilot.setId(1L);
        pilot.setName("Lewis Hamilton");
        pilot.setExperience(15);

        PilotSimpleResponse response = RaceService.mapToPilotSimpleResponse(pilot);

        assertNotNull(response);
        assertEquals(pilot.getId(), response.getId());
        assertEquals(pilot.getName(), response.getName());
        assertEquals(pilot.getExperience(), response.getExperience());
    }

    @Test
    void mapToResponse_WithPilotsAndCars_ReturnsCompleteResponse() {
        Race race = new Race();
        race.setId(1L);

        Pilot pilot = new Pilot();
        pilot.setId(1L);
        race.getPilots().add(pilot);

        Car car = new Car();
        car.setId(1L);
        race.getCars().add(car);

        when(pilotService.mapToResponse(any(Pilot.class))).thenReturn(new PilotResponse());
        when(carService.mapToResponse(any(Car.class))).thenReturn(new CarResponse());

        RaceResponse response = raceService.mapToResponse(race);

        assertNotNull(response);
        assertEquals(1, response.getPilots().size());
        assertEquals(1, response.getCars().size());
    }

    @Test
    void deleteRace_WithPilotsAndCars_CleansUpRelations() {
        Long raceId = 1L;
        Race race = new Race();
        race.setId(raceId);

        Pilot pilot = new Pilot();
        pilot.setId(1L);
        race.getPilots().add(pilot);

        Car car = new Car();
        car.setId(1L);
        race.getCars().add(car);

        when(raceRepository.findById(raceId)).thenReturn(Optional.of(race));

        raceService.deleteRace(raceId);

        assertTrue(race.getPilots().isEmpty());
        assertTrue(race.getCars().isEmpty());
        verify(raceRepository).delete(race);
    }

}
