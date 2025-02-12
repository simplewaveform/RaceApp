package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.CarDao;
import com.example.raceapp.model.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Transactional
public class CarDaoImpl implements CarDao {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Car save(Car car) {
        if (car.getId() == null) {
            entityManager.persist(car);
            return car;
        } else {
            return entityManager.merge(car);
        }
    }

    @Override
    public Optional<Car> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Car.class, id));
    }

    @Override
    public List<Car> findAll() {
        return entityManager.createQuery("SELECT c FROM Car c", Car.class).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Car car = entityManager.find(Car.class, id);
        if (car != null) {
            entityManager.remove(car);
        }
    }
}
