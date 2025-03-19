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

    /**
     * Maps a Car entity to a CarResponse DTO.
     *
     * @param car the Car entity to be mapped
     * @return a CarResponse DTO containing the car details
     */
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

    /**
     * Creates a new car based on the provided CarDto.
     *
     * @param request the CarDto containing the car data
     * @return a CarResponse DTO of the newly created car
     */
    @Caching(evict = {
        @CacheEvict(value = "cars", allEntries = true),
        @CacheEvict(value = "pilots", allEntries = true)
    })
    public CarResponse createCar(CarDto request) {
        Car car = new Car();
        return getCarResponse(request, car);
    }

    /**
     * Helper method to map a CarDto to a Car entity and save it.
     *
     * @param request the CarDto containing the car data
     * @param car the Car entity to be populated and saved
     * @return a CarResponse DTO of the saved car
     */
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

    /**
     * Retrieves cars with a minimum power filter and pagination support.
     *
     * @param minPower the minimum power of the cars to be retrieved
     * @param pageable the pagination details
     * @return a Page of CarResponse DTOs matching the filter
     */
    @Cacheable(value = "cars", key = "{#minPower, #pageable.pageNumber, #pageable.pageSize}")
    public Page<CarResponse> getCarsByPower(Integer minPower, Pageable pageable) {
        return carRepository.findCarsByPowerNative(minPower, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Searches for cars based on the given filter criteria and provides pagination support.
     *
     * @param brand the brand of the cars to search for
     * @param model the model of the cars to search for
     * @param power the power of the cars to search for
     * @param ownerId the owner ID to filter cars by
     * @param pageable the pagination details
     * @return a Page of CarResponse DTOs matching the filter
     */
    @Cacheable(value = "cars", key = "{#brand, #model, #power, #ownerId,"
            + "#pageable.pageNumber, #pageable.pageSize}")
    public Page<CarResponse> searchCarsWithPagination(
            String brand,
            String model,
            Integer power,
            Long ownerId,
            Pageable pageable
    ) {
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

        return carRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    /**
     * Retrieves a set of cars by their IDs.
     *
     * @param ids the set of car IDs to retrieve
     * @return a Set of Car entities matching the provided IDs
     */
    public Set<Car> getCarsByIds(Set<Long> ids) {
        return new HashSet<>(carRepository.findAllById(ids));
    }

    /**
     * Retrieves a car by its ID.
     *
     * @param id the ID of the car to retrieve
     * @return an Optional containing the CarResponse DTO if found, otherwise empty
     */
    @Cacheable(value = "cars", key = "#id")
    public Optional<CarResponse> getCarById(Long id) {
        return carRepository.findById(id).map(this::mapToResponse);
    }

    /**
     * Updates an existing car with new data.
     *
     * @param id the ID of the car to update
     * @param request the CarDto containing the new data
     * @return an Optional containing the updated CarResponse DTO if the car is found
     */
    @Caching(evict = {
        @CacheEvict(value = "cars", allEntries = true),
        @CacheEvict(value = "pilots", allEntries = true)
    })
    public Optional<CarResponse> updateCar(Long id, CarDto request) {
        return carRepository.findById(id).map(car -> getCarResponse(request, car));
    }

    /**
     * Partially updates a car with new data for specific fields.
     *
     * @param id the ID of the car to update
     * @param updates a map containing field names and values to update
     * @return an Optional containing the updated CarResponse DTO
     */
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

    /**
     * Deletes a car by its ID and removes references to it in related entities.
     *
     * @param id the ID of the car to delete
     */
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
