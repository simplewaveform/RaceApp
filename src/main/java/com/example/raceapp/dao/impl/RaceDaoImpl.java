package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.RaceDao;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the RaceDao interface for performing CRUD operations on Race entities.
 * Utilizes JPA and EntityManager for database interactions.
 */
@Repository
@Transactional
public class RaceDaoImpl implements RaceDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves a given race to the database. If the race is new (no ID), it persists the race,
     * otherwise, it merges the race entity.
     *
     * @param race the race entity to be saved.
     * @return the saved race entity.
     */
    @Override
    public Race save(Race race) {
        if (race.getId() == null) {
            entityManager.persist(race);
            return race;
        } else {
            return entityManager.merge(race);
        }
    }

    /**
     * Finds a race by its ID.
     *
     * @param id the ID of the race to retrieve.
     * @return an Optional containing the race if found, or empty if not found.
     */
    @Override
    public Optional<Race> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Race.class, id));
    }

    /**
     * Retrieves all races from the database.
     *
     * @return a list of all races.
     */
    @Override
    public List<Race> findAll() {
        return entityManager.createQuery("SELECT r FROM Race r", Race.class).getResultList();
    }

    /**
     * Deletes a race by its ID.
     *
     * @param id the ID of the race to delete.
     */
    @Override
    public void deleteById(Long id) {
        Race race = entityManager.find(Race.class, id);
        if (race != null) {
            entityManager.remove(race);
        }
    }

    /**
     * Retrieves all pilots participating in a specific race.
     *
     * @param raceId the ID of the race for which to retrieve the pilots.
     * @return a list of pilots participating in the specified race.
     */
    @Override
    public List<Pilot> getPilotsForRace(Long raceId) {
        return entityManager.createQuery(
                        "SELECT r.pilots FROM Race r WHERE r.id = :raceId", Pilot.class)
                .setParameter("raceId", raceId)
                .getResultList();
    }
}
