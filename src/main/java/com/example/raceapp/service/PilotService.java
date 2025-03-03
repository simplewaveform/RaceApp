package com.example.raceapp.service;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.PilotRepository;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Pilot entities.
 * Provides business logic for creating, retrieving, updating, and deleting pilots.
 */
@Service
@Transactional
public class PilotService {
    private final PilotRepository pilotRepository;

    /**
     * Constructs a new PilotService with the specified PilotRepository.
     *
     * @param pilotRepository The repository responsible for pilot-related database operations.
     */
    public PilotService(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    /**
     * Creates a new pilot and saves it to the database.
     *
     * @param pilot The pilot object to be created.
     * @return The created pilot object.
     */
    public Pilot createPilot(Pilot pilot) {
        return pilotRepository.save(pilot);
    }

    /**
     * Retrieves a pilot by its unique identifier.
     *
     * @param id The unique identifier of the pilot to retrieve.
     * @return An Optional containing the pilot if found, or an empty Optional if not found.
     */
    public Optional<Pilot> getPilotById(Long id) {
        return pilotRepository.findById(id);
    }

    /**
     * Searches for pilots based on optional filtering criteria.
     * If no filtering criteria are provided, retrieves all pilots.
     *
     * @param name       (Optional) The name of the pilot to filter by.
     * @param age        (Optional) The age of the pilot to filter by.
     * @param experience (Optional) The experience of the pilot to filter by.
     * @return A list of pilots matching the provided filtering criteria, or all pilots if no
     *         criteria are provided.
     */
    public List<Pilot> searchPilots(String name, Integer age, Integer experience) {
        List<Pilot> pilots = new ArrayList<>();

        if (name != null) {
            pilots.addAll(pilotRepository.findByName(name));
        }
        if (age != null) {
            pilots.addAll(pilotRepository.findByAge(age));
        }
        if (experience != null) {
            pilots.addAll(pilotRepository.findByExperience(experience));
        }

        // Если ни один критерий не был указан, вернуть всех пилотов
        if (name == null && age == null && experience == null) {
            return pilotRepository.findAll();
        }

        // Удаление дубликатов
        return new ArrayList<>(new HashSet<>(pilots));
    }

    /**
     * Updates an existing pilot by replacing its data with the provided pilot object.
     * The pilot ID in the path must match the ID of the pilot being updated.
     *
     * @param id          The unique identifier of the pilot to update.
     * @param pilotDetails The updated pilot object containing new data.
     * @return An Optional containing the updated pilot if successful, or an empty Optional if not
     *         found.
     */
    public Optional<Pilot> updatePilot(Long id, Pilot pilotDetails) {
        return pilotRepository.findById(id).map(pilot -> {
            pilot.setName(pilotDetails.getName());
            pilot.setAge(pilotDetails.getAge());
            pilot.setExperience(pilotDetails.getExperience());
            return pilotRepository.save(pilot);
        });
    }

    /**
     * Partially updates an existing pilot.
     * Only the fields present in the provided pilot object will be updated.
     *
     * @param id          The unique identifier of the pilot to partially update.
     * @param pilotDetails The partial pilot object containing updated fields.
     * @return An Optional containing the updated pilot if successful, or an empty Optional if not
     *         found.
     */
    public Optional<Pilot> partialUpdatePilot(Long id, Pilot pilotDetails) {
        return pilotRepository.findById(id).map(pilot -> {
            if (pilotDetails.getName() != null) {
                pilot.setName(pilotDetails.getName());
            }
            if (pilotDetails.getAge() != null) {
                pilot.setAge(pilotDetails.getAge());
            }
            if (pilotDetails.getExperience() != null) {
                pilot.setExperience(pilotDetails.getExperience());
            }
            return pilotRepository.save(pilot);
        });
    }

    /**
     * Deletes a pilot by its unique identifier.
     *
     * @param id The unique identifier of the pilot to delete.
     */
    public void deletePilot(Long id) {
        pilotRepository.deleteById(id);
    }
}