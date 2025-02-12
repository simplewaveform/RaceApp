package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.CarDao;
import com.example.raceapp.model.Car;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the {@link CarDao} interface using JPA with {@link EntityManager}.
 */
@Repository
@Transactional
public class CarDaoImpl implements CarDao {
    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a car entity. If the car ID is null, it is persisted as a new entity;
     * otherwise, it is merged into the existing entity.
     *
     * @param car the {@link Car} object to save
     * @return the saved or updated {@link Car} object
     */
    @Override
    public Car save(Car car) {
        if (car.getId() == null) {
            entityManager.persist(car);
            return car;
        } else {
            return entityManager.merge(car);
        }
    }

    /**
     * Finds a car by its ID.
     *
     * @param id the car ID
     * @return an {@link Optional} containing the found {@link Car}, or empty if not found
     */
    @Override
    public Optional<Car> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Car.class, id));
    }

    /**
     * Retrieves all cars from the database.
     *
     * @return a list of all {@link Car} entities
     */
    @Override
    public List<Car> findAll() {
        return entityManager.createQuery("SELECT c FROM Car c", Car.class).getResultList();
    }

    /**
     * Deletes a car by its ID if it exists.
     *
     * @param id the car ID
     */
    @Override
    public void deleteById(Long id) {
        Car car = entityManager.find(Car.class, id);
        if (car != null) {
            entityManager.remove(car);
        }
    }

    /**
     * Retrieves cars by their brand.
     *
     * @param brand the brand of the cars to filter by
     * @return a list of {@link Car} entities with the specified brand
     */
    @Override
    public List<Car> findByBrand(String brand) {
        return entityManager.createQuery("SELECT c FROM Car c WHERE c.brand = :brand", Car.class)
                .setParameter("brand", brand)
                .getResultList();
    }
}