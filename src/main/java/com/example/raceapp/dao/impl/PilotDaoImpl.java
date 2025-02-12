package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.PilotDao;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
        return entityManager.createQuery("SELECT p FROM Pilot p", Pilot.class).getResultList();
    }

    @Override
    public void deleteById(Long id) {
        Pilot pilot = entityManager.find(Pilot.class, id);
        if (pilot != null) {
            entityManager.remove(pilot);
        }
    }

    @Override
    public List<Race> getRacesForPilot(Long pilotId) {
        return entityManager.createQuery(
                        "SELECT p.races FROM Pilot p WHERE p.id = :pilotId", Race.class)
                .setParameter("pilotId", pilotId)
                .getResultList();
    }
}
