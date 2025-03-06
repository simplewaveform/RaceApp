package com.example.raceapp.service;

import com.example.raceapp.dto.CarDto;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.PilotRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing cars.
 */
@Service
@Transactional
public class CarService {
    private final CarRepository carRepository;
    private final PilotRepository pilotRepository;

    /**
     * Constructs a CarService with the specified repositories.
     *
     * @param carRepository the repository for car data operations.
     * @param pilotRepository the repository for pilot data operations.
     */
    public CarService(CarRepository carRepository, PilotRepository pilotRepository) {
        this.carRepository = carRepository;
        this.pilotRepository = pilotRepository;
    }

    private CarDto mapToCarDto(Car car) {
        CarDto carDto = new CarDto();
        carDto.setId(car.getId());
        carDto.setBrand(car.getBrand());
        carDto.setModel(car.getModel());
        carDto.setPower(car.getPower());
        if (car.getOwner() != null) {
            carDto.setOwnerId(car.getOwner().getId());
        }
        return carDto;
    }

    private Car mapToCar(CarDto carDto) {
        Car car = new Car();
        car.setBrand(carDto.getBrand());
        car.setModel(carDto.getModel());
        car.setPower(carDto.getPower());
        if (carDto.getOwnerId() != null) {
            Pilot pilot = pilotRepository.findById(carDto.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Pilot not found"));
            car.setOwner(pilot);
        }
        return car;
    }

    /**
     * Creates a new car.
     *
     * @param carDto Dto with car data.
     * @return Created car Dto.
     */
    public CarDto createCar(CarDto carDto) {
        Car car = mapToCar(carDto);
        return mapToCarDto(carRepository.save(car));
    }

    /**
     * Retrieves a car by ID.
     *
     * @param id Car ID.
     * @return Optional containing car Dto.
     */
    public Optional<CarDto> getCarById(Long id) {
        return carRepository.findById(id).map(this::mapToCarDto);
    }

    /**
     * Searches cars by filters.
     *
     * @param brand Brand filter.
     * @param model Model filter.
     * @param power Power filter.
     * @param ownerId Owner ID filter.
     * @return List of matching cars.
     */
    public List<CarDto> searchCars(String brand, String model, Integer power, Long ownerId) {
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
        return carRepository.findAll(spec).stream().map(this::mapToCarDto).toList();
    }

    /**
     * Updates a car.
     *
     * @param id Car ID.
     * @param carDto Updated data.
     * @return Optional containing updated car Dto.
     */
    public Optional<CarDto> updateCar(Long id, CarDto carDto) {
        return carRepository.findById(id).map(car -> {
            car.setBrand(carDto.getBrand());
            car.setModel(carDto.getModel());
            car.setPower(carDto.getPower());
            if (carDto.getOwnerId() != null) {
                Pilot pilot = pilotRepository.findById(carDto.getOwnerId())
                        .orElseThrow(() -> new IllegalArgumentException("Pilot not found"));
                car.setOwner(pilot);
            }
            return mapToCarDto(carRepository.save(car));
        });
    }

    /**
     * Deletes a car by ID.
     *
     * @param id Car ID.
     */
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}