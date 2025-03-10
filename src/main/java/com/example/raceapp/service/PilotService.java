package com.example.raceapp.service;

import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.dto.CarSimpleResponse;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.RaceRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service for managing pilot-related operations including creation, retrieval,
 * updating, and deletion of pilots. Handles mapping between entities and DTOs.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PilotService {
    private final PilotRepository pilotRepository;
    private final RaceRepository raceRepository;

    /**
     * Maps a Pilot entity to a PilotResponse DTO.
     *
     * @param pilot the Pilot entity to convert
     * @return PilotResponse containing pilot details and associated cars
     */
    PilotResponse mapToResponse(Pilot pilot) {
        PilotResponse response = new PilotResponse();
        response.setId(pilot.getId());
        response.setName(pilot.getName());
        response.setAge(pilot.getAge());
        response.setExperience(pilot.getExperience());
        response.setCars(pilot.getCars().stream()
                .map(this::mapToCarSimpleResponse)
                .toList());
        return response;
    }

    /**
     * Maps a Car entity to a CarSimpleResponse DTO.
     *
     * @param car the Car entity to convert
     * @return CarSimpleResponse containing basic car details
     */
    private CarSimpleResponse mapToCarSimpleResponse(Car car) {
        CarSimpleResponse response = new CarSimpleResponse();
        response.setId(car.getId());
        response.setBrand(car.getBrand());
        response.setModel(car.getModel());
        response.setPower(car.getPower());
        return response;
    }

    /**
     * Creates a new pilot from the provided request data.
     *
     * @param request the PilotRequest containing pilot details
     * @return PilotResponse with the created pilot's details
     */
    public PilotResponse createPilot(PilotDto request) {
        Pilot pilot = new Pilot();
        pilot.setName(request.getName());
        pilot.setAge(request.getAge());
        pilot.setExperience(request.getExperience());
        return mapToResponse(pilotRepository.save(pilot));
    }

    /**
     * Retrieves all pilots with their associated cars.
     *
     * @return List of PilotResponse containing all pilots
     */
    public List<PilotResponse> getAllPilots() {
        return pilotRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Searches pilots based on optional filters.
     *
     * @param name the name filter (optional)
     * @param age the age filter (optional)
     * @param experience the experience filter (optional)
     * @return List of PilotResponse matching the criteria
     */
    public List<PilotResponse> searchPilots(String name, Integer age, Integer experience) {
        Specification<Pilot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) predicates.add(cb.equal(root.get("name"), name));
            if (age != null) predicates.add(cb.equal(root.get("age"), age));
            if (experience != null) predicates.add(cb.equal(root.get("experience"), experience));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return pilotRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves a pilot by ID.
     *
     * @param id the ID of the pilot to retrieve
     * @return Optional containing PilotResponse if found, empty otherwise
     */
    public Optional<PilotResponse> getPilotById(Long id) {
        return pilotRepository.findById(id)
                .map(this::mapToResponse);
    }

    /**
     * Updates an existing pilot with new data.
     *
     * @param id the ID of the pilot to update
     * @param request the PilotRequest containing updated data
     * @return Optional containing updated PilotResponse if found, empty otherwise
     */
    public Optional<PilotResponse> updatePilot(Long id, PilotDto request) {
        return pilotRepository.findById(id).map(pilot -> {
            pilot.setName(request.getName());
            pilot.setAge(request.getAge());
            pilot.setExperience(request.getExperience());
            return mapToResponse(pilotRepository.save(pilot));
        });
    }

    /**
     * Partially updates specific fields of a pilot.
     *
     * @param id the ID of the pilot to update
     * @param updates map containing fields to update
     * @return Optional containing updated PilotResponse if found, empty otherwise
     * @throws IllegalArgumentException if invalid field is provided
     */
    public Optional<PilotResponse> partialUpdatePilot(Long id, Map<String, Object> updates) {
        return pilotRepository.findById(id).map(pilot -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> pilot.setName((String) value);
                    case "age" -> pilot.setAge((Integer) value);
                    case "experience" -> pilot.setExperience((Integer) value);
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }
            });
            return mapToResponse(pilotRepository.save(pilot));
        });
    }

    /**
     * Deletes a pilot by ID.
     *
     * @param id the ID of the pilot to delete
     */
    public void deletePilot(Long id) {
        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pilot not found with id: " + id));

        for (Race race : pilot.getRaces()) {
            race.getPilots().remove(pilot);
            raceRepository.save(race);
        }

        pilotRepository.delete(pilot);
    }

}