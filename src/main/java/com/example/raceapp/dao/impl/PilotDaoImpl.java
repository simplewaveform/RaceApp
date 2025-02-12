package com.example.raceapp.dao.impl;

import com.example.raceapp.dao.PilotDao;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * Implementation of the PilotDao interface for managing Pilot entities.
 * Provides methods to save, find, delete pilots and retrieve races for a pilot.
 */
@Repository
@Transactional
public class PilotDaoImpl implements PilotDao {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Saves the given pilot to the database.
     * If the pilot has no ID, it will be persisted. Otherwise, it will be merged.
     *
     * @param pilot the pilot to save
     * @return the saved or merged pilot entity
     */
    @Override
    public Pilot save(Pilot pilot) {
        if (pilot.getId() == null) {
            entityManager.persist(pilot);
            return pilot;
        } else {
            return entityManager.merge(pilot);
        }
    }

    /**
     * Finds a pilot by their ID.
     *
     * @param id the ID of the pilot to find
     * @return an Optional containing the found pilot, or an empty Optional if not found
     */
    @Override
    public Optional<Pilot> findById(Long id) {
        return Optional.ofNullable(entityManager.find(Pilot.class, id));
    }

    /**
     * Finds all pilots in the database.
     *
     * @return a list of all pilots
     */
    @Override
    public List<Pilot> findAll() {
        return entityManager.createQuery("SELECT p FROM Pilot p", Pilot.class).getResultList();
    }

    /**
     * Deletes a pilot by their ID.
     *
     * @param id the ID of the pilot to delete
     */
    @Override
    public void deleteById(Long id) {
        Pilot pilot = entityManager.find(Pilot.class, id);
        if (pilot != null) {
            entityManager.remove(pilot);
        }
    }

    /**
     * Retrieves all races a specific pilot is participating in.
     *
     * @param pilotId the ID of the pilot
     * @return a list of races the pilot is part of
     */
    @Override
    public List<Race> getRacesForPilot(Long pilotId) {
        return entityManager.createQuery(
                        "SELECT p.races FROM Pilot p WHERE p.id = :pilotId", Race.class)
                .setParameter("pilotId", pilotId)
                .getResultList();
    }
}
