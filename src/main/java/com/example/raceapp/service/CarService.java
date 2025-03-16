package com.example.raceapp.service;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
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
 * Service class for managing car-related operations including creation, retrieval,
 * updating, deletion, and querying of cars. Handles car-ownership associations and
 * mapping between {@link Car} entities and {@link CarResponse} DTOs.
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

    /**
     * Clears the cache for cars to ensure data consistency.
     * This is used after creating, updating, or deleting a car.
     */
    private void clearCarCache() {
        cache.evictByKeyPattern(CACHE_PREFIX);
    }

    /**
     * Maps a {@link Car} entity to a {@link CarResponse} DTO.
     *
     * @param car the {@link Car} entity to map.
     * @return a {@link CarResponse} DTO representing the car.
     */
    private CarResponse mapToResponse(Car car) {
        return RaceService.getCarResponse(car);
    }

    /**
     * Creates a new car based on the provided {@link CarDto}.
     *
     * @param request the {@link CarDto} containing the car details to be created.
     * @return a {@link CarResponse} DTO representing the created car.
     */
    public CarResponse createCar(CarDto request) {
        clearCarCache();
        Car car = new Car();
        return getCarResponse(request, car);
    }

    /**
     * Helper method to convert a {@link CarDto} to a {@link Car} entity and save it.
     *
     * @param request the {@link CarDto} containing car details.
     * @param car the {@link Car} entity to update.
     * @return a {@link CarResponse} DTO representing the saved car.
     */
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

    /**
     * Searches for cars based on the provided filters (brand, model, power, ownerId).
     * Caches the result to avoid redundant queries.
     *
     * @param brand the brand of the car to search for (nullable).
     * @param model the model of the car to search for (nullable).
     * @param power the power of the car to search for (nullable).
     * @param ownerId the owner ID to filter cars by (nullable).
     * @return a {@link List} of {@link CarResponse} DTOs matching the search criteria.
     */
    public Page<CarResponse> searchCarsWithPagination(
            String brand,
            String model,
            Integer power,
            Long ownerId,
            Pageable pageable
    ) {
        String cacheKey = String.format(
                "%sSEARCH_%s_%s_%s_%s_PAGE_%d_SIZE_%d",
                CACHE_PREFIX,
                brand, model, power, ownerId,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<CarResponse> cached = cache.get(cacheKey);
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
                predicates.add(cb.equal(root.get("owner.id"), ownerId));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Car> cars = carRepository.findAll(spec, pageable);
        Page<CarResponse> result = cars.map(this::mapToResponse);
        cache.put(cacheKey, result);
        return result;
    }

    /**
     * Retrieves cars with a power greater than the specified minimum power.
     * The result is paginated and cached.
     *
     * @param minPower the minimum power of the cars to retrieve.
     * @param pageable the pagination parameters.
     * @return a {@link Page} of {@link CarResponse} DTOs representing
     *         the cars with power greater than the specified threshold.
     */
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

    /**
     * Retrieves a car by its ID.
     *
     * @param id the ID of the car to retrieve.
     * @return an {@link Optional} containing the {@link CarResponse}
     *         DTO if the car is found, or {@link Optional#empty()} if not found.
     */
    public Optional<CarResponse> getCarById(Long id) {
        return carRepository.findById(id).map(this::mapToResponse);
    }

    /**
     * Updates a car with the details provided in the {@link CarDto}.
     *
     * @param id the ID of the car to update.
     * @param request the {@link CarDto} containing the updated car details.
     * @return an {@link Optional} containing the updated {@link CarResponse} DTO.
     */
    public Optional<CarResponse> updateCar(Long id, CarDto request) {
        clearCarCache();
        return carRepository.findById(id).map(car -> getCarResponse(request, car));
    }

    /**
     * Partially updates a car with the provided fields and values.
     *
     * @param id the ID of the car to update.
     * @param updates a map containing field names and their corresponding values to update.
     * @return an {@link Optional} containing the updated {@link CarResponse} DTO.
     */
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

    /**
     * Deletes a car by its ID.
     * This method removes the car from any associated races and from the owner's car list.
     *
     * @param id the ID of the car to delete.
     * @throws IllegalArgumentException if the car with the given ID is not found.
     */
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
