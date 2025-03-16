package com.example.raceapp.service;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import com.example.raceapp.utils.CacheManager;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing car-related operations including creation, retrieval,
 * updating, and deletion of cars. Handles owner associations and mapping between
 * entities and DTOs.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class CarService {
    private final CarRepository carRepository;
    private final RaceRepository raceRepository;
    private final PilotRepository pilotRepository;
    private final CacheManager cache;
    private static final String CACHE_PREFIX = "CAR_";
    private static final String PILOT_NOT_FOUND = "Pilot not found with id: ";

    private void clearCarCache() {
        cache.evictByKeyPattern(CACHE_PREFIX);
    }

    private CarResponse mapToResponse(Car car) {
        return RaceService.getCarResponse(car);
    }

    public CarResponse createCar(CarDto request) {
        clearCarCache();
        Car car = new Car();
        return getCarResponse(request, car);
    }

    private CarResponse getCarResponse(CarDto request, Car car) {
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setPower(request.getPower());

        if (request.getOwnerId() != null) {
            Pilot owner = pilotRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException(PILOT_NOT_FOUND
                            + request.getOwnerId()));
            car.setOwner(owner);
        }

        return mapToResponse(carRepository.save(car));
    }

    public List<CarResponse> searchCars(String brand, String model, Integer power, Long ownerId) {
        String cacheKey = CACHE_PREFIX + "SEARCH_" + brand + model + power + ownerId;
        List<CarResponse> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Specification<Car> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (brand != null) {
                predicates.add(cb.equal(root.get("brand"), brand));
            }
            if (model != null) {
                predicates.add(cb.equal(root.get("model"), model));
            }
            if (power != null) {
                predicates.add(cb.equal(root.get("power"), power));
            }
            if (ownerId != null) {
                predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        List<CarResponse> result = carRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .toList();

        cache.put(cacheKey, result);
        return result;
    }

    public Page<CarResponse> getCarsByPower(Integer minPower, Pageable pageable) {
        String cacheKey = String.format("%sPOWER_%d_PAGE_%d_SIZE_%d", CACHE_PREFIX, minPower,
                pageable.getPageNumber(), pageable.getPageSize());
        Page<CarResponse> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

        Page<CarResponse> result = carRepository.findCarsByPowerNative(minPower, pageable)
                .map(this::mapToResponse);

        cache.put(cacheKey, result);
        return result;
    }

    public Optional<CarResponse> getCarById(Long id) {
        return carRepository.findById(id).map(this::mapToResponse);
    }

    public Optional<CarResponse> updateCar(Long id, CarDto request) {
        clearCarCache();
        return carRepository.findById(id).map(car -> getCarResponse(request, car));
    }

    public Optional<CarResponse> partialUpdateCar(Long id, Map<String, Object> updates) {
        clearCarCache();
        return carRepository.findById(id).map(car -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "brand" -> car.setBrand((String) value);
                    case "model" -> car.setModel((String) value);
                    case "power" -> car.setPower((Integer) value);
                    case "ownerId" -> {
                        Long ownerId = ((Number) value).longValue();
                        Pilot owner = pilotRepository.findById(ownerId)
                                .orElseThrow(() -> new IllegalArgumentException(PILOT_NOT_FOUND
                                        + ownerId));
                        car.setOwner(owner);
                    }
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }
            });
            return mapToResponse(carRepository.save(car));
        });
    }

    public void deleteCar(Long id) {
        clearCarCache();
        Car car = carRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Car not found with id: " + id));

        for (Race race : car.getRaces()) {
            race.getCars().remove(car);
            raceRepository.save(race);
        }

        if (car.getOwner() != null) {
            car.getOwner().getCars().remove(car);
            pilotRepository.save(car.getOwner());
        }

        carRepository.delete(car);
    }
}
