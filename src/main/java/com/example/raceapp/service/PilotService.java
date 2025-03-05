package com.example.raceapp.service;

import com.example.raceapp.dto.PilotDTO;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.PilotRepository;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing pilots.
 */
@Service
@Transactional
public class PilotService {
    private final PilotRepository pilotRepository;

    public PilotService(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    /**
     * Maps a Pilot entity to a PilotDTO.
     * @param pilot The entity to convert.
     * @return Corresponding DTO.
     */
    private PilotDTO mapToPilotDTO(Pilot pilot) {
        PilotDTO dto = new PilotDTO();
        dto.setId(pilot.getId());
        dto.setName(pilot.getName());
        dto.setAge(pilot.getAge());
        dto.setExperience(pilot.getExperience());
        return dto;
    }

    /**
     * Creates a new pilot.
     * @param pilotDTO DTO containing pilot data.
     * @return Created pilot DTO.
     */
    public PilotDTO createPilot(PilotDTO pilotDTO) {
        Pilot pilot = new Pilot();
        pilot.setName(pilotDTO.getName());
        pilot.setAge(pilotDTO.getAge());
        pilot.setExperience(pilotDTO.getExperience());
        return mapToPilotDTO(pilotRepository.save(pilot));
    }

    /**
     * Retrieves all pilots.
     * @return List of all pilots.
     */
    public List<PilotDTO> getAllPilots() {
        return pilotRepository.findAll().stream()
                .map(this::mapToPilotDTO)
                .toList();
    }

    /**
     * Searches pilots by optional filters.
     * @param name Pilot name filter.
     * @param age Pilot age filter.
     * @param experience Pilot experience filter.
     * @return List of filtered pilots.
     */
    public List<PilotDTO> searchPilots(String name, Integer age, Integer experience) {
        Specification<Pilot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) predicates.add(cb.equal(root.get("name"), name));
            if (age != null) predicates.add(cb.equal(root.get("age"), age));
            if (experience != null) predicates.add(cb.equal(root.get("experience"), experience));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return pilotRepository.findAll(spec).stream()
                .map(this::mapToPilotDTO)
                .toList();
    }

    /**
     * Retrieves a pilot by ID.
     * @param id Pilot ID.
     * @return Optional containing pilot DTO.
     */
    public Optional<PilotDTO> getPilotById(Long id) {
        return pilotRepository.findById(id).map(this::mapToPilotDTO);
    }

    /**
     * Updates an existing pilot.
     * @param id Pilot ID.
     * @param pilotDTO Updated pilot data.
     * @return Optional containing updated pilot DTO.
     */
    public Optional<PilotDTO> updatePilot(Long id, PilotDTO pilotDTO) {
        return pilotRepository.findById(id).map(pilot -> {
            pilot.setName(pilotDTO.getName());
            pilot.setAge(pilotDTO.getAge());
            pilot.setExperience(pilotDTO.getExperience());
            return mapToPilotDTO(pilotRepository.save(pilot));
        });
    }

    /**
     * Deletes a pilot by ID.
     * @param id Pilot ID.
     */
    public void deletePilot(Long id) {
        pilotRepository.deleteById(id);
    }
}