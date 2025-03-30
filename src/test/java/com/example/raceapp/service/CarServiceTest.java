package com.example.raceapp.service;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.CarRepository;
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

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private PilotRepository pilotRepository;

    @Mock
    private RaceRepository raceRepository;

    @InjectMocks
    private CarService carService;

    @Test
    void getCarsByPower_ValidPower_ReturnsPage() {
        Integer minPower = 800;
        Pageable pageable = Pageable.unpaged();
        Car car = new Car();
        car.setId(1L);
        car.setPower(900);

        when(carRepository.findCarsByPowerNative(minPower, pageable))
                .thenReturn(new PageImpl<>(List.of(car)));

        Page<CarResponse> result = carService.getCarsByPower(minPower, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchCarsWithPagination_WithFilters_ReturnsFilteredPage() {
        String brand = "Ferrari";
        String model = "SF-23";
        Integer power = 950;
        Long ownerId = 1L;
        Pageable pageable = Pageable.unpaged();
        Car car = new Car();
        car.setId(1L);

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(car)));

        Page<CarResponse> result = carService.searchCarsWithPagination(
                brand, model, power, ownerId, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getCarsByIds_ValidIds_ReturnsCars() {
        Set<Long> ids = Set.of(1L, 2L);
        Car car1 = new Car();
        car1.setId(1L);
        Car car2 = new Car();
        car2.setId(2L);

        when(carRepository.findAllById(ids)).thenReturn(List.of(car1, car2));

        Set<Car> result = carService.getCarsByIds(ids);

        assertEquals(2, result.size());
    }

    @Test
    void deleteCar_WithRacesAndOwner_CleansUpRelations() {
        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);

        Pilot owner = new Pilot();
        owner.setId(1L);
        car.setOwner(owner);
        owner.getCars().add(car);

        Race race = new Race();
        race.setId(1L);
        race.getCars().add(car);
        car.getRaces().add(race);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        carService.deleteCar(carId);

        verify(raceRepository).save(race);
        assertTrue(race.getCars().isEmpty());
        verify(pilotRepository).save(owner);
        assertTrue(owner.getCars().isEmpty());
        verify(carRepository).delete(car);
    }

    @Test
    void mapToResponse_WithOwner_ReturnsCompleteResponse() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Ferrari");
        car.setModel("SF-23");
        car.setPower(950);

        Pilot owner = new Pilot();
        owner.setId(1L);
        owner.setName("Charles Leclerc");
        car.setOwner(owner);

        CarResponse response = carService.mapToResponse(car);

        assertNotNull(response);
        assertEquals(car.getId(), response.getId());
        assertNotNull(response.getOwner());
        assertEquals(owner.getId(), response.getOwner().getId());
    }

    @Test
    void mapToResponse_WithoutOwner_ReturnsResponseWithoutOwner() {
        Car car = new Car();
        car.setId(1L);
        car.setBrand("Ferrari");
        car.setModel("SF-23");
        car.setPower(950);

        CarResponse response = carService.mapToResponse(car);

        assertNotNull(response);
        assertNull(response.getOwner());
    }

    @Test
    void createCar_ValidInput_ReturnsCarResponse() {
        CarDto dto = new CarDto();
        dto.setBrand("Ferrari");
        dto.setModel("SF-23");
        dto.setPower(950);
        dto.setOwnerId(1L);

        Pilot owner = new Pilot();
        owner.setId(1L);

        when(pilotRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> {
            Car car = inv.getArgument(0);
            car.setId(1L);
            return car;
        });

        CarResponse result = carService.createCar(dto);

        assertNotNull(result);
        assertEquals(dto.getBrand(), result.getBrand());
        verify(pilotRepository).findById(1L);
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void createCar_InvalidOwnerId_ThrowsNotFoundException() {
        CarDto dto = new CarDto();
        dto.setOwnerId(999L);

        when(pilotRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> carService.createCar(dto));
    }

    @Test
    void updateCar_NonExistingId_ReturnsEmpty() {
        Long carId = 999L;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        Optional<CarResponse> result = carService.updateCar(carId, new CarDto());

        assertTrue(result.isEmpty());
    }

    @Test
    void getCarById_NonExistingId_ReturnsEmpty() {
        Long carId = 999L;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());

        Optional<CarResponse> result = carService.getCarById(carId);

        assertTrue(result.isEmpty());
    }

    @Test
    void setRaces_ShouldSetRacesCorrectly() {
        Car car = new Car();
        Race race1 = new Race();
        race1.setId(1L);
        Race race2 = new Race();
        race2.setId(2L);

        Set<Race> races = Set.of(race1, race2);
        car.setRaces(races);

        assertEquals(2, car.getRaces().size());
        assertTrue(car.getRaces().contains(race1));
        assertTrue(car.getRaces().contains(race2));
    }

    @Test
    void searchCars_WithNullParameters_ReturnsAll() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Car(), new Car())));

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(null, null, null, null, pageable);

        // Assert
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void deleteCar_WithoutRaces_DoesNotUpdateRaces() {
        // Arrange
        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // Act
        carService.deleteCar(carId);

        // Assert
        verify(raceRepository, never()).save(any());
        verify(carRepository).delete(car);
    }

    @Test
    void searchCars_WithBrandOnly_ReturnsFiltered() {
        // Arrange
        String brand = "Ferrari";
        Pageable pageable = Pageable.unpaged();

        Car car = new Car();
        car.setBrand(brand);
        Page<Car> mockPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                brand, // brand
                null,  // model
                null,  // power
                null,  // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(brand, result.getContent().getFirst().getBrand());
    }

    @Test
    void searchCars_WithBrandAndModel_ReturnsFiltered() {
        // Arrange
        String brand = "Ferrari";
        String model = "SF-23";
        Pageable pageable = Pageable.unpaged();

        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        Page<Car> mockPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                brand, // brand
                model, // model
                null,  // power
                null,  // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(brand, result.getContent().getFirst().getBrand());
        assertEquals(model, result.getContent().getFirst().getModel());
    }

    @Test
    void searchCars_WithPowerAndOwnerId_ReturnsFiltered() {
        // Arrange
        Integer power = 800;
        Long ownerId = 1L;
        Pageable pageable = Pageable.unpaged();

        Pilot owner = new Pilot();
        owner.setId(ownerId);

        Car car = new Car();
        car.setPower(power);
        car.setOwner(owner);
        Page<Car> mockPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                null,    // brand
                null,    // model
                power,   // power
                ownerId, // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(power, result.getContent().getFirst().getPower());
        assertEquals(ownerId, result.getContent().getFirst().getOwner().getId());
    }

    @Test
    void searchCars_WithAllNullParameters_ReturnsAll() {
        // Arrange
        Pageable pageable = Pageable.unpaged();
        Page<Car> mockPage = new PageImpl<>(List.of(new Car(), new Car()));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                null, // brand
                null, // model
                null, // power
                null, // ownerId
                pageable
        );

        // Assert
        assertEquals(2, result.getTotalElements());
    }

    @Test
    void searchCars_withBrandAndPower_returnsFilteredResults() {
        // Arrange
        String brand = "Ferrari";
        Integer power = 800;
        Pageable pageable = PageRequest.of(0, 10);

        Car car = new Car();
        car.setBrand(brand);
        car.setPower(power);
        Page<Car> mockPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                brand, // brand
                null,  // model
                power, // power
                null,  // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(brand, result.getContent().getFirst().getBrand());
        assertEquals(power, result.getContent().getFirst().getPower());
    }

    @Test
    void searchCars_withModelAndOwnerId_returnsFilteredResults() {
        // Arrange
        String model = "SF-23";
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Pilot owner = new Pilot();
        owner.setId(ownerId);

        Car car = new Car();
        car.setModel(model);
        car.setOwner(owner);
        Page<Car> mockPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                null,    // brand
                model,   // model
                null,    // power
                ownerId, // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(model, result.getContent().getFirst().getModel());
        assertEquals(ownerId, result.getContent().getFirst().getOwner().getId());
    }

    @Test
    void searchCars_withBrandModelPower_returnsFilteredResults() {
        // Arrange
        String brand = "Ferrari";
        String model = "SF-23";
        Integer power = 800;
        Pageable pageable = PageRequest.of(0, 10);

        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        car.setPower(power);
        Page<Car> mockPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                brand,  // brand
                model,  // model
                power,  // power
                null,   // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(brand, result.getContent().getFirst().getBrand());
        assertEquals(model, result.getContent().getFirst().getModel());
        assertEquals(power, result.getContent().getFirst().getPower());
    }

    @Test
    void searchCars_withModelPowerOwnerId_returnsFilteredResults() {
        // Arrange
        String model = "SF-23";
        Integer power = 800;
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Pilot owner = new Pilot();
        owner.setId(ownerId);

        Car car = new Car();
        car.setModel(model);
        car.setPower(power);
        car.setOwner(owner);
        Page<Car> mockPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                null,    // brand
                model,   // model
                power,   // power
                ownerId, // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(model, result.getContent().getFirst().getModel());
        assertEquals(power, result.getContent().getFirst().getPower());
        assertEquals(ownerId, result.getContent().getFirst().getOwner().getId());
    }

    @Test
    void searchCars_withAllFilters_returnsFilteredResults() {
        // Arrange
        String brand = "Ferrari";
        String model = "SF-23";
        Integer power = 800;
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        Pilot owner = new Pilot();
        owner.setId(ownerId);

        Car car = new Car();
        car.setBrand(brand);
        car.setModel(model);
        car.setPower(power);
        car.setOwner(owner);
        Page<Car> mockPage = new PageImpl<>(List.of(car));

        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                brand,   // brand
                model,   // model
                power,   // power
                ownerId, // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(brand, result.getContent().getFirst().getBrand());
        assertEquals(model, result.getContent().getFirst().getModel());
        assertEquals(power, result.getContent().getFirst().getPower());
        assertEquals(ownerId, result.getContent().getFirst().getOwner().getId());
    }

    @Test
    void searchCars_specificationWithBrand_createsCorrectPredicate() {
        // Arrange
        String brand = "Ferrari";
        Pageable pageable = PageRequest.of(0, 10);

        ArgumentCaptor<Specification<Car>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(carRepository.findAll(specCaptor.capture(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Car())));

        // Act
        carService.searchCarsWithPagination(
                brand, // brand
                null,  // model
                null,  // power
                null,  // ownerId
                pageable
        );

        // Assert
        Specification<Car> spec = specCaptor.getValue();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Car> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Path path = mock(Path.class);

        when(root.get("brand")).thenReturn(path);

        spec.toPredicate(root, query, cb);

        verify(cb).equal(path, brand);
        verify(cb, never()).equal(any(), any(Integer.class));
        verify(cb, never()).equal(any(), any(Long.class));
    }

    @Test
    void searchCars_specificationWithOwnerId_createsCorrectPredicate() {
        // Arrange
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);

        ArgumentCaptor<Specification<Car>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(carRepository.findAll(specCaptor.capture(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Car())));

        // Act
        carService.searchCarsWithPagination(
                null,    // brand
                null,    // model
                null,    // power
                ownerId, // ownerId
                pageable
        );

        // Assert
        Specification<Car> spec = specCaptor.getValue();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Car> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Path ownerPath = mock(Path.class);
        Path idPath = mock(Path.class);

        when(root.get("owner")).thenReturn(ownerPath);
        when(ownerPath.get("id")).thenReturn(idPath);

        spec.toPredicate(root, query, cb);

        verify(cb).equal(idPath, ownerId);
        verify(cb, never()).equal(any(), any(String.class));
        verify(cb, never()).equal(any(), any(Integer.class));
    }

    @Test
    void getCarById_ExistingId_ReturnsCarResponse() {
        // Arrange
        Long carId = 1L;
        Car car = new Car();
        car.setId(carId);
        car.setBrand("Ferrari");

        when(carRepository.findById(carId)).thenReturn(Optional.of(car));

        // Act
        Optional<CarResponse> result = carService.getCarById(carId);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(carId, result.get().getId());
        assertEquals("Ferrari", result.get().getBrand());
    }

    @Test
    void updateCar_ExistingId_ReturnsUpdatedCarResponse() {
        // Arrange
        Long carId = 1L;
        CarDto request = new CarDto();
        request.setBrand("Updated Ferrari");
        request.setModel("SF-24");
        request.setPower(1000);
        request.setOwnerId(1L);

        Car existingCar = new Car();
        existingCar.setId(carId);
        existingCar.setBrand("Ferrari");
        existingCar.setModel("SF-23");
        existingCar.setPower(950);

        Pilot owner = new Pilot();
        owner.setId(1L);

        when(carRepository.findById(carId)).thenReturn(Optional.of(existingCar));
        when(pilotRepository.findById(1L)).thenReturn(Optional.of(owner));
        when(carRepository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        Optional<CarResponse> result = carService.updateCar(carId, request);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(carId, result.get().getId());
        assertEquals("Updated Ferrari", result.get().getBrand());
        assertEquals("SF-24", result.get().getModel());
        assertEquals(1000, result.get().getPower());
        assertEquals(1L, result.get().getOwner().getId());
    }

    @Test
    void deleteCar_NonExistingId_ThrowsNotFoundException() {
        Long carId = 999L;
        when(carRepository.findById(carId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> carService.deleteCar(carId));
    }

    @Test
    void createCar_WithoutOwnerId_ReturnsCarResponse() {
        // Arrange
        CarDto dto = new CarDto();
        dto.setBrand("Ferrari");
        dto.setModel("SF-23");
        dto.setPower(950);
        // Не устанавливаем ownerId

        when(carRepository.save(any(Car.class))).thenAnswer(inv -> {
            Car car = inv.getArgument(0);
            car.setId(1L);
            return car;
        });

        // Act
        CarResponse result = carService.createCar(dto);

        // Assert
        assertNotNull(result);
        assertEquals(dto.getBrand(), result.getBrand());
        assertEquals(dto.getModel(), result.getModel());
        assertEquals(dto.getPower(), result.getPower());
        assertNull(result.getOwner()); // Проверяем, что владелец не установлен

        // Проверяем, что метод findById у pilotRepository не был вызван
        verify(pilotRepository, never()).findById(any());
        // Проверяем, что был вызван метод save у carRepository
        verify(carRepository).save(any(Car.class));
    }

    @Test
    void searchCars_withModelAndPower_returnsFilteredResults() {
        // Arrange
        String model = "SF-23";
        Integer power = 800;
        Pageable pageable = PageRequest.of(0, 10);
        Car car = new Car();
        car.setModel(model);
        car.setPower(power);
        Page<Car> mockPage = new PageImpl<>(List.of(car));
        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);
        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                null,    // brand
                model,   // model
                power,   // power
                null,    // ownerId
                pageable
        );
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(model, result.getContent().getFirst().getModel());
        assertEquals(power, result.getContent().getFirst().getPower());
    }

    @Test
    void searchCars_withBrandAndOwnerId_returnsFilteredResults() {
        // Arrange
        String brand = "Ferrari";
        Long ownerId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        Pilot owner = new Pilot();
        owner.setId(ownerId);
        Car car = new Car();
        car.setBrand(brand);
        car.setOwner(owner);
        Page<Car> mockPage = new PageImpl<>(List.of(car));
        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(mockPage);
        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                brand,   // brand
                null,    // model
                null,    // power
                ownerId, // ownerId
                pageable
        );
        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(brand, result.getContent().getFirst().getBrand());
        assertEquals(ownerId, result.getContent().getFirst().getOwner().getId());
    }

    @Test
    void searchCars_WithModelOnly_ReturnsFiltered() {
        // Arrange
        String model = "SF-23";
        Pageable pageable = Pageable.unpaged();

        Car car = new Car();
        car.setModel(model);

        // Правильно: возвращаем PageImpl напрямую
        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(car)));

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                null,   // brand
                model,  // model
                null,   // power
                null,   // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(model, result.getContent().getFirst().getModel());
    }

    @Test
    void searchCars_specificationWithModel_createsCorrectPredicate() {
        String model = "SF-23";
        Pageable pageable = PageRequest.of(0, 10);

        ArgumentCaptor<Specification<Car>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(carRepository.findAll(specCaptor.capture(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Car())));

        carService.searchCarsWithPagination(
                null,   // brand
                model,  // model
                null,   // power
                null,   // ownerId
                pageable
        );

        Specification<Car> spec = specCaptor.getValue();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Car> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Path path = mock(Path.class);

        when(root.get("model")).thenReturn(path);

        spec.toPredicate(root, query, cb);

        verify(cb).equal(path, model); // Проверяем, что предикат для model был добавлен
    }

    @Test
    void searchCars_WithPowerOnly_ReturnsFiltered() {
        // Arrange
        Integer power = 800;
        Pageable pageable = Pageable.unpaged();

        Car car = new Car();
        car.setPower(power);

        // Мокируем репозиторий, возвращаем PageImpl с тестовыми данными
        when(carRepository.findAll(any(Specification.class), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(car)));

        // Act
        Page<CarResponse> result = carService.searchCarsWithPagination(
                null,   // brand
                null,  // model
                power, // power
                null,  // ownerId
                pageable
        );

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(power, result.getContent().getFirst().getPower());
    }

    @Test
    void searchCars_specificationWithPower_createsCorrectPredicate() {
        // Arrange
        Integer power = 800;
        Pageable pageable = PageRequest.of(0, 10);

        ArgumentCaptor<Specification<Car>> specCaptor = ArgumentCaptor.forClass(Specification.class);
        when(carRepository.findAll(specCaptor.capture(), eq(pageable)))
                .thenReturn(new PageImpl<>(List.of(new Car())));

        // Act
        carService.searchCarsWithPagination(
                null,
                null,   // model
                power,  // power
                null,   // ownerId
                pageable
        );

        // Assert
        Specification<Car> spec = specCaptor.getValue();
        CriteriaBuilder cb = mock(CriteriaBuilder.class);
        Root<Car> root = mock(Root.class);
        CriteriaQuery<?> query = mock(CriteriaQuery.class);
        Path path = mock(Path.class);

        when(root.get("power")).thenReturn(path);

        spec.toPredicate(root, query, cb);

        verify(cb).equal(path, power); // Проверяем, что предикат для power был добавлен
    }

}