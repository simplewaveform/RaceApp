package com.example.raceapp.service;

import com.example.raceapp.dao.RaceDao;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing Race entities.
 * Provides business logic for Race CRUD operations.
 */
@Service
public class RaceService {
    private final RaceDao raceDao;

    /**
     * Constructor for the RaceService class.
     *
     * @param raceDao The RaceDao instance to be injected into this service.
     */
    @Autowired
    public RaceService(RaceDao raceDao) {
        this.raceDao = raceDao;
    }

    /**
     * Creates a new Race entity.
     *
     * @param race the Race to create.
     * @return the saved Race entity.
     */
    public Race createRace(Race race) {
        return raceDao.save(race);
    }

    /**
     * Retrieves a Race by its ID.
     *
     * @param id the ID of the Race.
     * @return an Optional containing the Race if found, or empty otherwise.
     */
    public Optional<Race> getRaceById(Long id) {
        return raceDao.findById(id);
    }

    /**
     * Retrieves all Races.
     *
     * @return a list of all Races.
     */
    public List<Race> getAllRaces() {
        return raceDao.findAll();
    }

    /**
     * Fully updates a Race entity.
     *
     * @param id the ID of the Race.
     * @param raceDetails the new Race data.
     * @return an Optional containing the updated Race if found, or empty otherwise.
     */
    public Optional<Race> updateRace(Long id, Race raceDetails) {
        return raceDao.findById(id).map(race -> {
            race.setName(raceDetails.getName());
            race.setYear(raceDetails.getYear());
            return raceDao.save(race);
        });
    }

    /**
     * Partially updates a Race entity.
     *
     * @param id the ID of the Race.
     * @param raceDetails the partial Race data.
     * @return an Optional containing the updated Race if found, or empty otherwise.
     */
    public Optional<Race> partialUpdateRace(Long id, Race raceDetails) {
        return raceDao.findById(id).map(race -> {
            if (raceDetails.getName() != null) {
                race.setName(raceDetails.getName());
            }
            if (raceDetails.getYear() != 0) {
                race.setYear(raceDetails.getYear());
            }
            return raceDao.save(race);
        });
    }

    /**
     * Deletes a Race by its ID.
     *
     * @param id the ID of the Race to delete.
     */
    public void deleteRace(Long id) {
        raceDao.deleteById(id);
    }
}
