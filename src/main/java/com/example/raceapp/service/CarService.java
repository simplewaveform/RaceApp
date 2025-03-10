package com.example.raceapp.service;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.dto.PilotSimpleResponse;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
    private static final String PILOT_NOT_FOUND = "Pilot not found with id: ";

    /**
     * Maps a Car entity to a CarResponse DTO.
     *
     * @param car the Car entity to convert
     * @return CarResponse containing car details and owner information
     */
    private CarResponse mapToResponse(Car car) {
        CarResponse response = new CarResponse();
        response.setId(car.getId());
        response.setBrand(car.getBrand());
        response.setModel(car.getModel());
        response.setPower(car.getPower());

        if (car.getOwner() != null) {
            PilotSimpleResponse owner = new PilotSimpleResponse();
            owner.setId(car.getOwner().getId());
            owner.setName(car.getOwner().getName());
            owner.setExperience(car.getOwner().getExperience());
            response.setOwner(owner);
        }
        return response;
    }

    /**
     * Creates a new car from the provided request data.
     *
     * @param request the CarRequest containing car details
     * @return CarResponse with the created car's details
     * @throws IllegalArgumentException if owner ID is invalid
     */
    public CarResponse createCar(CarDto request) {
        Car car = new Car();
        car.setBrand(request.getBrand());
        car.setModel(request.getModel());
        car.setPower(request.getPower());

        if (request.getOwnerId() != null) {
            Pilot owner = pilotRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException(PILOT_NOT_FOUND + request.getOwnerId()));
            car.setOwner(owner);
        }

        return mapToResponse(carRepository.save(car));
    }

    /**
     * Retrieves all cars with their associated owners.
     *
     * @return List of CarResponse containing all cars
     */
    public List<CarResponse> getAllCars() {
        return carRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Searches cars based on optional filters.
     *
     * @param brand the brand filter (optional)
     * @param model the model filter (optional)
     * @param power the power filter (optional)
     * @param ownerId the owner ID filter (optional)
     * @return List of CarResponse matching the criteria
     */
    public List<CarResponse> searchCars(String brand, String model, Integer power, Long ownerId) {
        Specification<Car> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (brand != null) predicates.add(cb.equal(root.get("brand"), brand));
            if (model != null) predicates.add(cb.equal(root.get("model"), model));
            if (power != null) predicates.add(cb.equal(root.get("power"), power));
            if (ownerId != null) predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return carRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves a car by ID.
     *
     * @param id the ID of the car to retrieve
     * @return Optional containing CarResponse if found, empty otherwise
     */
    public Optional<CarResponse> getCarById(Long id) {
        return carRepository.findById(id)
                .map(this::mapToResponse);
    }

    /**
     * Updates an existing car with new data.
     *
     * @param id the ID of the car to update
     * @param request the CarRequest containing updated data
     * @return Optional containing updated CarResponse if found, empty otherwise
     * @throws IllegalArgumentException if owner ID is invalid
     */
    public Optional<CarResponse> updateCar(Long id, CarDto request) {
        return carRepository.findById(id).map(car -> {
            car.setBrand(request.getBrand());
            car.setModel(request.getModel());
            car.setPower(request.getPower());

            if (request.getOwnerId() != null) {
                Pilot owner = pilotRepository.findById(request.getOwnerId())
                        .orElseThrow(() -> new IllegalArgumentException(PILOT_NOT_FOUND + request.getOwnerId()));
                car.setOwner(owner);
            }

            return mapToResponse(carRepository.save(car));
        });
    }

    /**
     * Partially updates specific fields of a car.
     *
     * @param id the ID of the car to update
     * @param updates map containing fields to update
     * @return Optional containing updated CarResponse if found, empty otherwise
     * @throws IllegalArgumentException if invalid field is provided
     */
    public Optional<CarResponse> partialUpdateCar(Long id, Map<String, Object> updates) {
        return carRepository.findById(id).map(car -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "brand" -> car.setBrand((String) value);
                    case "model" -> car.setModel((String) value);
                    case "power" -> car.setPower((Integer) value);
                    case "ownerId" -> {
                        Long ownerId = ((Number) value).longValue();
                        Pilot owner = pilotRepository.findById(ownerId)
                                .orElseThrow(() -> new IllegalArgumentException(PILOT_NOT_FOUND + ownerId));
                        car.setOwner(owner);
                    }
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }
            });
            return mapToResponse(carRepository.save(car));
        });
    }

    /**
     * Deletes a car by ID.
     *
     * @param id the ID of the car to delete
     */
    public void deleteCar(Long id) {
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