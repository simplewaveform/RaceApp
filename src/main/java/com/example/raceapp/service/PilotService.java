package com.example.raceapp.service;

import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.PilotRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing pilots.
 */
@Service
@Transactional
public class PilotService {
    private final PilotRepository pilotRepository;

    /**
     * Constructs a PilotService with the specified repository.
     *
     * @param pilotRepository the repository for pilot data operations.
     */
    public PilotService(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    /**
     * Maps a Pilot entity to a PilotDto.
     *
     * @param pilot the entity to convert.
     * @return corresponding DTO.
     */
    private PilotDto mapToPilotDto(Pilot pilot) {
        PilotDto dto = new PilotDto();
        dto.setId(pilot.getId());
        dto.setName(pilot.getName());
        dto.setAge(pilot.getAge());
        dto.setExperience(pilot.getExperience());
        return dto;
    }

    /**
     * Creates a new pilot.
     *
     * @param pilotDto DTO containing pilot data.
     * @return created pilot DTO.
     */
    public PilotDto createPilot(PilotDto pilotDto) {
        Pilot pilot = new Pilot();
        pilot.setName(pilotDto.getName());
        pilot.setAge(pilotDto.getAge());
        pilot.setExperience(pilotDto.getExperience());
        return mapToPilotDto(pilotRepository.save(pilot));
    }

    /**
     * Retrieves all pilots.
     *
     * @return list of all pilot DTOs.
     */
    public List<PilotDto> getAllPilots() {
        return pilotRepository.findAll().stream().map(this::mapToPilotDto).toList();
    }

    /**
     * Searches pilots by optional filters.
     *
     * @param name the name filter (optional).
     * @param age the age filter (optional).
     * @param experience the experience filter (optional).
     * @return list of matching pilot DTOs.
     */
    public List<PilotDto> searchPilots(String name, Integer age, Integer experience) {
        Specification<Pilot> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) {
                predicates.add(cb.equal(root.get("name"), name));
            }
            if (age != null) {
                predicates.add(cb.equal(root.get("age"), age));
            }
            if (experience != null) {
                predicates.add(cb.equal(root.get("experience"), experience));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
        return pilotRepository.findAll(spec).stream().map(this::mapToPilotDto).toList();
    }

    /**
     * Retrieves a pilot by ID.
     *
     * @param id the ID of the pilot to retrieve.
     * @return Optional containing the pilot DTO, or empty if not found.
     */
    public Optional<PilotDto> getPilotById(Long id) {
        return pilotRepository.findById(id).map(this::mapToPilotDto);
    }

    /**
     * Updates an existing pilot.
     *
     * @param id the ID of the pilot to update.
     * @param pilotDto DTO containing updated pilot data.
     * @return Optional containing the updated pilot DTO, or empty if not found.
     */
    public Optional<PilotDto> updatePilot(Long id, PilotDto pilotDto) {
        return pilotRepository.findById(id).map(pilot -> {
            pilot.setName(pilotDto.getName());
            pilot.setAge(pilotDto.getAge());
            pilot.setExperience(pilotDto.getExperience());
            return mapToPilotDto(pilotRepository.save(pilot));
        });
    }

    /**
     * Deletes a pilot by ID.
     *
     * @param id the ID of the pilot to delete.
     */
    public void deletePilot(Long id) {
        pilotRepository.deleteById(id);
    }
}