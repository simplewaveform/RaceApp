package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.RaceDao;
import com.example.raceapp.model.Race;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Implementation of RaceDao using JPA and EntityManager.
 */
@Repository
@Transactional
public class RaceDaoImpl implements RaceDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Race save(Race race) {
        if (race.getId() == null) {
            entityManager.persist(race);
            return race;
        } else {
            return entityManager.merge(race);
        }
    }

    @Override
    public Optional<Race> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Race.class, id));
    }

    @Override
    public List<Race> findAll() {
        return entityManager.createQuery("SELECT r FROM Race r", Race.class).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Race race = entityManager.find(Race.class, id);
        if (race != null) {
            entityManager.remove(race);
        }
    }
}
