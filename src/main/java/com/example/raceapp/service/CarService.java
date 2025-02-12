package com.example.raceapp.service;

import com.example.raceapp.dao.CarDao;
import com.example.raceapp.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CarService {
    private final CarDao carDao;

    @Autowired
    public CarService(CarDao carDao) {
        this.carDao = carDao;
    }

    public Car createCar(Car car) {
        return carDao.save(car);
    }

    public List<Car> getAllCars() {
        return carDao.findAll();
    }

    public Car getCarById(Long id) {
        Optional<Car> car = carDao.findById(id);
        return car.orElse(null);
    }

    public void deleteCar(Long id) {
        carDao.deleteById(id);
    }
}
