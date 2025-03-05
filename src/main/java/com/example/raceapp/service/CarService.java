package com.example.raceapp.service;

import com.example.raceapp.dto.CarDTO;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.PilotRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing cars.
 */
@Service
@Transactional
public class CarService {
    private final CarRepository carRepository;
    private final PilotRepository pilotRepository;

    public CarService(CarRepository carRepository, PilotRepository pilotRepository) {
        this.carRepository = carRepository;
        this.pilotRepository = pilotRepository;
    }

    private CarDTO mapToCarDTO(Car car) {
        CarDTO carDTO = new CarDTO();
        carDTO.setId(car.getId());
        carDTO.setBrand(car.getBrand());
        carDTO.setModel(car.getModel());
        carDTO.setPower(car.getPower());
        if (car.getOwner() != null) {
            carDTO.setOwnerId(car.getOwner().getId());
        }
        return carDTO;
    }

    private Car mapToCar(CarDTO carDTO) {
        Car car = new Car();
        car.setBrand(carDTO.getBrand());
        car.setModel(carDTO.getModel());
        car.setPower(carDTO.getPower());
        if (carDTO.getOwnerId() != null) {
            Pilot pilot = pilotRepository.findById(carDTO.getOwnerId())
                    .orElseThrow(() -> new IllegalArgumentException("Pilot not found"));
            car.setOwner(pilot);
        }
        return car;
    }

    /**
     * Creates a new car.
     * @param carDTO DTO with car data.
     * @return Created car DTO.
     */
    public CarDTO createCar(CarDTO carDTO) {
        Car car = mapToCar(carDTO);
        return mapToCarDTO(carRepository.save(car));
    }

    /**
     * Retrieves a car by ID.
     * @param id Car ID.
     * @return Optional containing car DTO.
     */
    public Optional<CarDTO> getCarById(Long id) {
        return carRepository.findById(id).map(this::mapToCarDTO);
    }

    /**
     * Searches cars by filters.
     * @param brand Brand filter.
     * @param model Model filter.
     * @param power Power filter.
     * @param ownerId Owner ID filter.
     * @return List of matching cars.
     */
    public List<CarDTO> searchCars(String brand, String model, Integer power, Long ownerId) {
        Specification<Car> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (brand != null) predicates.add(cb.equal(root.get("brand"), brand));
            if (model != null) predicates.add(cb.equal(root.get("model"), model));
            if (power != null) predicates.add(cb.equal(root.get("power"), power));
            if (ownerId != null) predicates.add(cb.equal(root.get("owner").get("id"), ownerId));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return carRepository.findAll(spec).stream().map(this::mapToCarDTO).toList();
    }

    /**
     * Updates a car.
     * @param id Car ID.
     * @param carDTO Updated data.
     * @return Optional containing updated car DTO.
     */
    public Optional<CarDTO> updateCar(Long id, CarDTO carDTO) {
        return carRepository.findById(id).map(car -> {
            car.setBrand(carDTO.getBrand());
            car.setModel(carDTO.getModel());
            car.setPower(carDTO.getPower());
            if (carDTO.getOwnerId() != null) {
                Pilot pilot = pilotRepository.findById(carDTO.getOwnerId())
                        .orElseThrow(() -> new IllegalArgumentException("Pilot not found"));
                car.setOwner(pilot);
            }
            return mapToCarDTO(carRepository.save(car));
        });
    }

    /**
     * Deletes a car by ID.
     * @param id Car ID.
     */
    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}