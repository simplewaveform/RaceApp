package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.CarDao;
import com.example.raceapp.model.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Implementation of CarDao using JPA and EntityManager.
 */
@Repository
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
    public List<Car> findByBrand(String brand) {
        TypedQuery<Car> query = entityManager.createQuery(
                "SELECT c FROM Car c WHERE c.brand = :brand", Car.class);
        query.setParameter("brand", brand);
        return query.getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Car car = entityManager.find(Car.class, id);
        if (car != null) {
            entityManager.remove(car);
        }
    }
}
