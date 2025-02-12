package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.RaceDao;
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

    @Override
    public List<Pilot> getPilotsForRace(Long raceId) {
        return entityManager.createQuery(
                        "SELECT r.pilots FROM Race r WHERE r.id = :raceId", Pilot.class)
                .setParameter("raceId", raceId)
                .getResultList();
    }
}
