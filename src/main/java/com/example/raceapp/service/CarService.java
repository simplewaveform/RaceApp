package com.example.raceapp.service;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing car-related operations.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final PilotRepository pilotRepository;
    private final RaceRepository raceRepository;

    CarResponse mapToResponse(Car car) {
        CarResponse response = new CarResponse();
        response.setId(car.getId());
        response.setBrand(car.getBrand());
        response.setModel(car.getModel());
        response.setPower(car.getPower());
        if (car.getOwner() != null) {
            response.setOwner(RaceService.mapToPilotSimpleResponse(car.getOwner()));
        }
        return response;
    }

    @Caching(evict = {
        @CacheEvict(value = "cars", allEntries = true),
        @CacheEvict(value = "pilots", allEntries = true)
    })
    public CarResponse createCar(CarDto request) {
        Car car = new Car();
        return getCarResponse(request, car);
    }

    private CarResponse getCarResponse(CarDto request, Car car) {
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setPower(request.getPower());
        if (request.getOwnerId() != null) {
            Pilot owner = pilotRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Pilot not found"));
            car.setOwner(owner);
        }
        return mapToResponse(carRepository.save(car));
    }

    @Cacheable(value = "cars", key = "{#minPower, #pageable.pageNumber, #pageable.pageSize}")
    public Page<CarResponse> getCarsByPower(Integer minPower, Pageable pageable) {
        return carRepository.findCarsByPowerNative(minPower, pageable)
                .map(this::mapToResponse);
    }

    @Cacheable(value = "cars", key = "{#brand, #model, #power, #ownerId, #pageable.pageNumber, #pageable.pageSize}")
    public Page<CarResponse> searchCarsWithPagination(
            String brand,
            String model,
            Integer power,
            Long ownerId,
            Pageable pageable
    ) {
        Specification<Car> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (brand != null) predicates.add(cb.equal(root.get("brand"), brand));
            if (model != null) predicates.add(cb.equal(root.get("model"), model));
            if (power != null) predicates.add(cb.equal(root.get("power"), power));
            if (ownerId != null) predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return carRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    /**
     * Retrieves cars by their IDs.
     */
    public Set<Car> getCarsByIds(Set<Long> ids) {
        return new HashSet<>(carRepository.findAllById(ids));
    }

    @Cacheable(value = "cars", key = "#id")
    public Optional<CarResponse> getCarById(Long id) {
        return carRepository.findById(id).map(this::mapToResponse);
    }

    @Caching(evict = {
            @CacheEvict(value = "cars", allEntries = true),
            @CacheEvict(value = "pilots", allEntries = true)
    })
    public Optional<CarResponse> updateCar(Long id, CarDto request) {
        return carRepository.findById(id).map(car -> getCarResponse(request, car));
    }

    @Caching(evict = {
            @CacheEvict(value = "cars", allEntries = true),
            @CacheEvict(value = "pilots", allEntries = true)
    })
    public Optional<CarResponse> partialUpdateCar(Long id, Map<String, Object> updates) {
        return carRepository.findById(id).map(car -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "brand" -> car.setBrand((String) value);
                    case "model" -> car.setModel((String) value);
                    case "power" -> car.setPower((Integer) value);
                    case "ownerId" -> {
                        Pilot owner = pilotRepository.findById(((Number) value).longValue())
                                .orElseThrow(() -> new IllegalArgumentException("Pilot not found"));
                        car.setOwner(owner);
                    }
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }
            });
            return mapToResponse(carRepository.save(car));
        });
    }

    @Caching(evict = {
            @CacheEvict(value = "cars", allEntries = true),
            @CacheEvict(value = "pilots", allEntries = true),
            @CacheEvict(value = "races", allEntries = true)
    })
    public void deleteCar(Long id) {
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found"));

        car.getRaces().forEach(race -> {
            race.getCars().remove(car);
            raceRepository.save(race);
        });

        if (car.getOwner() != null) {
            car.getOwner().getCars().remove(car);
            pilotRepository.save(car.getOwner());
        }

        carRepository.delete(car);
    }
}