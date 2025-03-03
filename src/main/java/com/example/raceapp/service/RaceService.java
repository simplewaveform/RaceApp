package com.example.raceapp.service;

import com.example.raceapp.model.Race;
import com.example.raceapp.repository.RaceRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Race entities.
 * Provides business logic for creating, retrieving, updating, and deleting races.
 */
@Service
@Transactional
public class RaceService {
    private final RaceRepository raceRepository;

    /**
     * Constructs a new RaceService with the specified RaceRepository.
     *
     * @param raceRepository The repository responsible for
     *                       handling race-related database operations.
     */
    public RaceService(RaceRepository raceRepository) {
        this.raceRepository = raceRepository;
    }

    /**
     * Creates a new race and saves it to the database.
     *
     * @param race The race object to be created.
     * @return The created race object.
     */
    public Race createRace(Race race) {
        return raceRepository.save(race);
    }

    /**
     * Retrieves a race by its unique identifier.
     * Uses an EntityGraph to eagerly fetch related entities (pilots and cars).
     *
     * @param id The unique identifier of the race to retrieve.
     * @return An Optional containing the race if found, or an empty Optional if not found.
     */
    public Optional<Race> getRaceById(Long id) {
        return raceRepository.findById(id); // Использует EntityGraph
    }

    /**
     * Searches for races based on optional filtering criteria.
     * If no filtering criteria are provided, retrieves all races.
     *
     * @param name (Optional) The name of the race to filter by.
     * @param year (Optional) The year of the race to filter by.
     * @return A list of races matching the provided filtering criteria, or all races if no
     *         criteria are provided.
     */
    public List<Race> searchRaces(String name, Integer year) {
        List<Race> races = new ArrayList<>();

        if (name != null) {
            races.addAll(raceRepository.findByName(name));
        }
        if (year != null) {
            races.addAll(raceRepository.findByYear(year));
        }

        // Если ни один критерий не был указан, вернуть все гонки
        if (name == null && year == null) {
            return raceRepository.findAll();
        }

        // Удаление дубликатов
        return new ArrayList<>(new HashSet<>(races));
    }

    /**
     * Updates an existing race by replacing its data with the provided race object.
     * The race ID in the path must match the ID of the race being updated.
     *
     * @param id The unique identifier of the race to update.
     * @param raceDetails The updated race object containing new data.
     * @return An Optional containing the updated race if successful, or an empty Optional if not
     *         found.
     */
    public Optional<Race> updateRace(Long id, Race raceDetails) {
        return raceRepository.findById(id).map(race -> {
            race.setName(raceDetails.getName());
            race.setYear(raceDetails.getYear());
            return raceRepository.save(race);
        });
    }

    /**
     * Partially updates an existing race.
     * Only the fields present in the provided race object will be updated.
     *
     * @param id The unique identifier of the race to partially update.
     * @param raceDetails The partial race object containing updated fields.
     * @return An Optional containing the updated race if successful, or an empty Optional if not
     *         found.
     */
    public Optional<Race> partialUpdateRace(Long id, Race raceDetails) {
        return raceRepository.findById(id).map(race -> {
            if (raceDetails.getName() != null) {
                race.setName(raceDetails.getName());
            }
            if (raceDetails.getYear() != null) {
                race.setYear(raceDetails.getYear());
            }
            return raceRepository.save(race);
        });
    }

    /**
     * Deletes a race by its unique identifier.
     *
     * @param id The unique identifier of the race to delete.
     */
    public void deleteRace(Long id) {
        raceRepository.deleteById(id);
    }
}