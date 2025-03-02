package com.example.raceapp.service;

import com.example.raceapp.model.Car;
import com.example.raceapp.repository.CarRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car createCar(Car car) {
        return carRepository.save(car);
    }

    public Optional<Car> getCarById(Long id) {
        return carRepository.findById(id);
    }

    public List<Car> searchCars(String brand, String model, Integer power, Long ownerId) {
        if (brand != null || model != null || power != null || ownerId != null) {
            return carRepository.findByBrandOrModelOrPowerOrOwnerId(brand, model, power, ownerId);
        }
        return carRepository.findAll();
    }

    public Optional<Car> updateCar(Long id, Car carDetails) {
        return carRepository.findById(id).map(car -> {
            car.setBrand(carDetails.getBrand());
            car.setModel(carDetails.getModel());
            car.setPower(carDetails.getPower());
            return carRepository.save(car);
        });
    }

    public Optional<Car> partialUpdateCar(Long id, Car carDetails) {
        return carRepository.findById(id).map(car -> {
            if (carDetails.getBrand() != null && !carDetails.getBrand().isEmpty()) {
                car.setBrand(carDetails.getBrand());
            }
            if (carDetails.getModel() != null && !carDetails.getModel().isEmpty()) {
                car.setModel(carDetails.getModel());
            }
            if (carDetails.getPower() > 0) {
                car.setPower(carDetails.getPower());
            }
            return carRepository.save(car);
        });
    }

    public void deleteCar(Long id) {
        carRepository.deleteById(id);
    }
}