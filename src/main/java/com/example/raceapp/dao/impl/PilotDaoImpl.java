package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.PilotDao;
import com.example.raceapp.model.Pilot;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Implementation of PilotDao using JPA and EntityManager.
 */
@Repository
@Transactional
public class PilotDaoImpl implements PilotDao {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public Pilot save(Pilot pilot) {
        if (pilot.getId() == null) {
            entityManager.persist(pilot);
            return pilot;
        } else {
            return entityManager.merge(pilot);
        }
    }

    @Override
    public Optional<Pilot> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Pilot.class, id));
    }

    @Override
    public List<Pilot> findAll() {
        return entityManager.createQuery(
                        "SELECT DISTINCT p FROM Pilot p "
                                + "LEFT JOIN FETCH p.cars "
                                + "LEFT JOIN FETCH p.races", Pilot.class)
                .getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Pilot pilot = entityManager.find(Pilot.class, id);
        if (pilot != null) {
            entityManager.remove(pilot);
        }
    }
}