package com.example.raceapp.service;

import com.example.raceapp.dao.PilotDao;
import com.example.raceapp.model.Pilot;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing Pilot entities.
 * Provides business logic for Pilot CRUD operations.
 */
@Service
public class PilotService {
    private final PilotDao pilotDao;

    /**
     * Constructor for the PilotService class.
     *
     * @param pilotDao The PilotDao instance to be injected into this service.
     */
    @Autowired
    public PilotService(PilotDao pilotDao) {
        this.pilotDao = pilotDao;
    }

    /**
     * Creates a new Pilot entity.
     *
     * @param pilot the Pilot to create.
     * @return the saved Pilot entity.
     */
    public Pilot createPilot(Pilot pilot) {
        return pilotDao.save(pilot);
    }

    /**
     * Retrieves a Pilot by its ID.
     *
     * @param id the ID of the Pilot.
     * @return an Optional containing the Pilot if found, or empty otherwise.
     */
    public Optional<Pilot> getPilotById(Long id) {
        return pilotDao.findById(id);
    }

    /**
     * Retrieves all Pilots.
     *
     * @return a list of all Pilots.
     */
    public List<Pilot> getAllPilots() {
        return pilotDao.findAll();
    }

    /**
     * Fully updates a Pilot entity.
     *
     * @param id the ID of the Pilot.
     * @param pilotDetails the new Pilot data.
     * @return an Optional containing the updated Pilot if found, or empty otherwise.
     */
    public Optional<Pilot> updatePilot(Long id, Pilot pilotDetails) {
        return pilotDao.findById(id).map(pilot -> {
            pilot.setName(pilotDetails.getName());
            pilot.setAge(pilotDetails.getAge());
            pilot.setExperience(pilotDetails.getExperience());
            return pilotDao.save(pilot);
        });
    }

    /**
     * Partially updates a Pilot entity.
     *
     * @param id the ID of the Pilot.
     * @param pilotDetails the partial Pilot data.
     * @return an Optional containing the updated Pilot if found, or empty otherwise.
     */
    public Optional<Pilot> partialUpdatePilot(Long id, Pilot pilotDetails) {
        return pilotDao.findById(id).map(pilot -> {
            if (pilotDetails.getName() != null) {
                pilot.setName(pilotDetails.getName());
            }
            if (pilotDetails.getAge() != 0) {
                pilot.setAge(pilotDetails.getAge());
            }
            if (pilotDetails.getExperience() != 0) {
                pilot.setExperience(pilotDetails.getExperience());
            }
            return pilotDao.save(pilot);
        });
    }

    /**
     * Deletes a Pilot by its ID.
     *
     * @param id the ID of the Pilot to delete.
     */
    public void deletePilot(Long id) {
        pilotDao.deleteById(id);
    }
}