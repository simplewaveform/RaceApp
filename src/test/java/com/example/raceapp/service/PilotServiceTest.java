package com.example.raceapp.service;

import com.example.raceapp.dto.CarSimpleResponse;
import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PilotServiceTest {

    @Mock
    private PilotRepository pilotRepository;

    @Mock
    private RaceRepository raceRepository;

    @InjectMocks
    private PilotService pilotService;

    @Test
    void createPilot_ValidInput_ReturnsPilotResponse() {
        PilotDto dto = new PilotDto();
        dto.setName("Lewis Hamilton");
        dto.setAge(38);
        dto.setExperience(15);

        Pilot savedPilot = new Pilot();
        savedPilot.setId(1L);
        savedPilot.setName(dto.getName());
        savedPilot.setAge(dto.getAge());
        savedPilot.setExperience(dto.getExperience());

        when(pilotRepository.save(any(Pilot.class))).thenReturn(savedPilot);

        PilotResponse result = pilotService.createPilot(dto);

        assertNotNull(result);
        assertEquals(dto.getName(), result.getName());
        assertEquals(dto.getAge(), result.getAge());
        verify(pilotRepository).save(any(Pilot.class));
    }

    @Test
    void createPilotsBulk_ValidInput_ReturnsListOfResponses() {
        // Arrange
        PilotDto pilot1 = new PilotDto();
        pilot1.setName("Test Pilot 1");
        pilot1.setAge(25);
        pilot1.setExperience(5);

        PilotDto pilot2 = new PilotDto();
        pilot2.setName("Test Pilot 2");
        pilot2.setAge(30);
        pilot2.setExperience(10);

        Pilot savedPilot1 = new Pilot();
        savedPilot1.setId(1L);
        savedPilot1.setName(pilot1.getName());
        savedPilot1.setAge(pilot1.getAge());
        savedPilot1.setExperience(pilot1.getExperience());

        Pilot savedPilot2 = new Pilot();
        savedPilot2.setId(2L);
        savedPilot2.setName(pilot2.getName());
        savedPilot2.setAge(pilot2.getAge());
        savedPilot2.setExperience(pilot2.getExperience());

        when(pilotRepository.saveAll(anyList())).thenReturn(List.of(savedPilot1, savedPilot2));

        // Act
        List<PilotResponse> responses = pilotService.createPilotsBulk(List.of(pilot1, pilot2));

        // Assert
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("Test Pilot 1", responses.get(0).getName());
        assertEquals("Test Pilot 2", responses.get(1).getName());
        verify(pilotRepository).saveAll(anyList());
    }

    @Test
    void getPilotsByCarBrandNative_ValidBrand_ReturnsPage() {
        String brand = "Ferrari";
        Pageable pageable = Pageable.unpaged();
        Pilot pilot = new Pilot();
        pilot.setId(1L);
        Page<Pilot> page = new PageImpl<>(List.of(pilot));

        when(pilotRepository.findPilotsByCarBrandNative(brand, pageable)).thenReturn(page);

        Page<PilotResponse> result = pilotService.getPilotsByCarBrandNative(brand, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchPilotsWithPagination_WithFilters_ReturnsFilteredPage() {
        String name = "Max";
        Integer age = 25;
        Integer experience = 5;
        Pageable pageable = Pageable.unpaged();
        Pilot pilot = new Pilot();
        pilot.setId(1L);

        when(pilotRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pilot)));

        Page<PilotResponse> result = pilotService.searchPilotsWithPagination(name, age, experience, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getPilotById_ExistingId_ReturnsPilot() {
        Long pilotId = 1L;
        Pilot pilot = new Pilot();
        pilot.setId(pilotId);

        when(pilotRepository.findById(pilotId)).thenReturn(Optional.of(pilot));

        Optional<PilotResponse> result = pilotService.getPilotById(pilotId);

        assertTrue(result.isPresent());
        assertEquals(pilotId, result.get().getId());
    }

    @Test
    void getPilotById_NonExistingId_ReturnsEmpty() {
        Long pilotId = 999L;
        when(pilotRepository.findById(pilotId)).thenReturn(Optional.empty());

        Optional<PilotResponse> result = pilotService.getPilotById(pilotId);

        assertTrue(result.isEmpty());
    }

    @Test
    void getPilotsByIds_ValidIds_ReturnsPilots() {
        Set<Long> ids = Set.of(1L, 2L);
        Pilot pilot1 = new Pilot();
        pilot1.setId(1L);
        Pilot pilot2 = new Pilot();
        pilot2.setId(2L);

        when(pilotRepository.findAllById(ids)).thenReturn(List.of(pilot1, pilot2));

        Set<Pilot> result = pilotService.getPilotsByIds(ids);

        assertEquals(2, result.size());
    }

    @Test
    void updatePilot_ValidInput_UpdatesPilot() {
        Long pilotId = 1L;
        PilotDto dto = new PilotDto();
        dto.setName("Updated Name");
        dto.setAge(30);
        dto.setExperience(10);

        Pilot existingPilot = new Pilot();
        existingPilot.setId(pilotId);

        when(pilotRepository.findById(pilotId)).thenReturn(Optional.of(existingPilot));
        when(pilotRepository.save(any(Pilot.class))).thenReturn(existingPilot);

        Optional<PilotResponse> result = pilotService.updatePilot(pilotId, dto);

        assertTrue(result.isPresent());
        verify(pilotRepository).save(existingPilot);
    }

    @Test
    void updatePilot_NonExistingId_ReturnsEmpty() {
        Long pilotId = 999L;
        when(pilotRepository.findById(pilotId)).thenReturn(Optional.empty());

        Optional<PilotResponse> result = pilotService.updatePilot(pilotId, new PilotDto());

        assertTrue(result.isEmpty());
    }

    @Test
    void deletePilot_WithCarsAndRaces_CleansUpRelations() {
        Long pilotId = 1L;
        Pilot pilot = new Pilot();
        pilot.setId(pilotId);

        Car car = new Car();
        car.setId(1L);
        car.setOwner(pilot);
        pilot.getCars().add(car);

        Race race = new Race();
        race.setId(1L);
        race.getPilots().add(pilot);
        pilot.getRaces().add(race);

        when(pilotRepository.findById(pilotId)).thenReturn(Optional.of(pilot));

        pilotService.deletePilot(pilotId);

        verify(raceRepository).save(race);
        assertTrue(race.getPilots().isEmpty());
        assertNull(car.getOwner());
        verify(pilotRepository).delete(pilot);
    }

    @Test
    void mapToResponse_WithCars_ReturnsCompleteResponse() {
        Pilot pilot = new Pilot();
        pilot.setId(1L);

        Car car = new Car();
        car.setId(1L);
        pilot.getCars().add(car);

        PilotResponse response = pilotService.mapToResponse(pilot);

        assertNotNull(response);
        assertEquals(1, response.getCars().size());
    }

    @Test
    void mapToCarSimpleResponse_ValidCar_ReturnsResponse() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Ferrari");
        car.setBrand("F1");
        car.setBrand("900");

        CarSimpleResponse response = pilotService.mapToCarSimpleResponse(car);

        assertNotNull(response);
        assertEquals(car.getId(), response.getId());
        assertEquals(car.getBrand(), response.getBrand());
        assertEquals(car.getModel(), response.getModel());
        assertEquals(car.getPower(), response.getPower());
    }

    @Test
    void deletePilot_WithNoCarsOrRaces_ShouldStillDelete() {
        // Arrange
        Long pilotId = 1L;
        Pilot pilot = new Pilot();
        pilot.setId(pilotId);
        pilot.setCars(new HashSet<>());
        pilot.setRaces(new HashSet<>());

        when(pilotRepository.findById(pilotId)).thenReturn(Optional.of(pilot));

        // Act
        pilotService.deletePilot(pilotId);

        // Assert
        verify(pilotRepository).delete(pilot);
    }

    @Test
    void deletePilot_PilotNotFound_ShouldThrowException() {
        // Arrange
        Long invalidId = 999L;
        when(pilotRepository.findById(invalidId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> pilotService.deletePilot(invalidId));
    }

    @Test
    void searchPilots_withNameOnly_returnsFilteredResults() {
        String name = "Test";
        Pageable pageable = PageRequest.of(0, 10);
        Pilot pilot = new Pilot();
        pilot.setName(name);

        when(pilotRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pilot)));

        Page<PilotResponse> result = pilotService.searchPilotsWithPagination(name, null, null, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(name, result.getContent().get(0).getName());
    }

    @Test
    void searchPilots_withAgeOnly_returnsFilteredResults() {
        Integer age = 25;
        Pageable pageable = PageRequest.of(0, 10);
        Pilot pilot = new Pilot();
        pilot.setAge(age);

        when(pilotRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pilot)));

        Page<PilotResponse> result = pilotService.searchPilotsWithPagination(null, age, null, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(age, result.getContent().get(0).getAge());
    }

    @Test
    void searchPilots_withExperienceOnly_returnsFilteredResults() {
        Integer experience = 5;
        Pageable pageable = PageRequest.of(0, 10);
        Pilot pilot = new Pilot();
        pilot.setExperience(experience);

        when(pilotRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pilot)));

        Page<PilotResponse> result = pilotService.searchPilotsWithPagination(null, null, experience, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(experience, result.getContent().get(0).getExperience());
    }

    @Test
    void searchPilots_withAllFilters_returnsFilteredResults() {
        String name = "Test";
        Integer age = 25;
        Integer experience = 5;
        Pageable pageable = PageRequest.of(0, 10);

        Pilot pilot = new Pilot();
        pilot.setName(name);
        pilot.setAge(age);
        pilot.setExperience(experience);

        when(pilotRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pilot)));

        Page<PilotResponse> result = pilotService.searchPilotsWithPagination(name, age, experience, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(name, result.getContent().getFirst().getName());
        assertEquals(age, result.getContent().getFirst().getAge());
        assertEquals(experience, result.getContent().getFirst().getExperience());
    }

    @Test
    void searchPilots_withExperienceAndAge_returnsFilteredResults() {
        Integer age = 30;
        Integer experience = 10;
        Pageable pageable = PageRequest.of(0, 10);

        Pilot pilot = new Pilot();
        pilot.setAge(age);
        pilot.setExperience(experience);

        when(pilotRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pilot)));

        Page<PilotResponse> result = pilotService.searchPilotsWithPagination(null, age, experience, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(age, result.getContent().getFirst().getAge());
        assertEquals(experience, result.getContent().getFirst().getExperience());
    }

    @Test
    void searchPilots_withNameAndAge_returnsFilteredResults() {
        String name = "Test";
        Integer age = 25;
        Pageable pageable = PageRequest.of(0, 10);
        Pilot pilot = new Pilot();
        pilot.setName(name);
        pilot.setAge(age);

        when(pilotRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(pilot)));

        Page<PilotResponse> result = pilotService.searchPilotsWithPagination(name, age, null, pageable);

        assertEquals(1, result.getContent().size());
        assertEquals(name, result.getContent().get(0).getName());
        assertEquals(age, result.getContent().get(0).getAge());
    }

    @Test
    void searchPilots_withNoFilters_returnsAllResults() {
        Pageable pageable = PageRequest.of(0, 10);
        when(pilotRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Pilot(), new Pilot())));

        Page<PilotResponse> result = pilotService.searchPilotsWithPagination(null, null, null, pageable);

        assertEquals(2, result.getTotalElements());
    }

    @Test
    void deletePilot_withCarInMultipleRaces_cleansUpAllRelations() {
        Long pilotId = 1L;
        Pilot pilot = new Pilot();
        pilot.setId(pilotId);

        Car car = new Car();
        car.setOwner(pilot);
        Race race1 = new Race();
        Race race2 = new Race();
        car.getRaces().addAll(Set.of(race1, race2));
        pilot.getCars().add(car);

        when(pilotRepository.findById(pilotId)).thenReturn(Optional.of(pilot));

        pilotService.deletePilot(pilotId);

        verify(raceRepository, times(2)).save(any(Race.class));
        assertNull(car.getOwner());
    }

    @Test
    void deletePilot_withDirectRaceParticipation_removesFromRaces() {
        Long pilotId = 1L;
        Pilot pilot = new Pilot();
        pilot.setId(pilotId);

        Race race = new Race();
        race.getPilots().add(pilot);
        pilot.getRaces().add(race);

        when(pilotRepository.findById(pilotId)).thenReturn(Optional.of(pilot));

        pilotService.deletePilot(pilotId);

        verify(raceRepository).save(race);
        assertTrue(race.getPilots().isEmpty());
    }

    @Test
    void deletePilot_withCarNotInRaces_removesOwnerOnly() {
        Long pilotId = 1L;
        Pilot pilot = new Pilot();
        pilot.setId(pilotId);

        Car car = new Car();
        car.setOwner(pilot);
        pilot.getCars().add(car);

        when(pilotRepository.findById(pilotId)).thenReturn(Optional.of(pilot));

        pilotService.deletePilot(pilotId);

        verify(raceRepository, never()).save(any());
        assertNull(car.getOwner());
    }

    @Test
    void mapToResponse_fullDetails_returnsCompleteResponse() {
        Pilot pilot = new Pilot();
        pilot.setId(1L);
        pilot.setName("Test");
        pilot.setAge(25);
        pilot.setExperience(5);

        Car car1 = new Car();
        car1.setId(1L);
        Car car2 = new Car();
        car2.setId(2L);
        pilot.setCars(Set.of(car1, car2));

        PilotResponse response = pilotService.mapToResponse(pilot);

        assertEquals(pilot.getId(), response.getId());
        assertEquals(pilot.getName(), response.getName());
        assertEquals(pilot.getAge(), response.getAge());
        assertEquals(pilot.getExperience(), response.getExperience());
        assertEquals(2, response.getCars().size());
    }

    @Test
    void searchPilots_withNameAndExperience_returnsFilteredResults() {
        String name = "Test";
        Integer experience = 5;
        Pageable pageable = PageRequest.of(0, 10);

        ArgumentCaptor<Specification<Pilot>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(pilotRepository.findAll(specCaptor.capture(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Pilot())));

        pilotService.searchPilotsWithPagination(name, null, experience, pageable);

        Specification<Pilot> spec = specCaptor.getValue();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Pilot> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);

        when(root.get(any(String.class))).thenReturn(mock(Path.class));

        spec.toPredicate(root, query, cb);

        verify(cb).equal(any(), eq(name));
        verify(cb).equal(any(), eq(experience));
        verify(cb, never()).equal(any(), any());
    }

    @Test
    void searchPilots_withAgeAndExperience_returnsFilteredResults() {
        Integer age = 25;
        Integer experience = 5;
        Pageable pageable = PageRequest.of(0, 10);

        ArgumentCaptor<Specification<Pilot>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(pilotRepository.findAll(specCaptor.capture(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Pilot())));

        pilotService.searchPilotsWithPagination(null, age, experience, pageable);

        Specification<Pilot> spec = specCaptor.getValue();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Pilot> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);

        when(root.get(any(String.class))).thenReturn(mock(Path.class));

        spec.toPredicate(root, query, cb);

        verify(cb).equal(any(), eq(age));
        verify(cb).equal(any(), eq(experience));
        verify(cb, never()).equal(any(), any());
    }

    @Test
    void searchPilots_specificationWithExperience_createsCorrectPredicate() {
        // Arrange
        Integer experience = 5;
        Pageable pageable = PageRequest.of(0, 10);

        ArgumentCaptor<Specification<Pilot>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(pilotRepository.findAll(specCaptor.capture(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Pilot())));

        // Act
        pilotService.searchPilotsWithPagination(
                null,
                null,
                experience,
                pageable
        );

        // Assert
        Specification<Pilot> spec = specCaptor.getValue();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Pilot> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Path path = mock(Path.class);

        when(root.get("experience")).thenReturn(path);

        spec.toPredicate(root, query, cb);

        verify(cb).equal(path, experience);
    }

}