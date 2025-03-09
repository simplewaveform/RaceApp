package com.example.raceapp.service;

import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing races.
 */
@Service
@Transactional
public class RaceService {
    private final RaceRepository raceRepository;
    private final PilotRepository pilotRepository;

    /**
     * Constructs a RaceService with the specified repositories.
     *
     * @param raceRepository the repository for race data operations.
     * @param pilotRepository the repository for pilot data operations.
     */
    public RaceService(RaceRepository raceRepository, PilotRepository pilotRepository) {
        this.raceRepository = raceRepository;
        this.pilotRepository = pilotRepository;
    }

    /**
     * Maps a Race entity to a RaceDto.
     *
     * @param race the entity to convert.
     * @return corresponding DTO.
     */
    private RaceDto mapToRaceDto(Race race) {
        RaceDto dto = new RaceDto();
        dto.setId(race.getId());
        dto.setName(race.getName());
        dto.setYear(race.getYear());
        dto.setPilotIds(race.getPilots().stream()
                .map(Pilot::getId)
                .collect(Collectors.toSet()));
        return dto;
    }

    /**
     * Creates a new race.
     *
     * @param raceDto DTO containing race data.
     * @return created race DTO.
     */
    public RaceDto createRace(RaceDto raceDto) {
        Race race = new Race();
        race.setName(raceDto.getName());
        race.setYear(raceDto.getYear());

        Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(raceDto.getPilotIds()));
        race.setPilots(pilots);

        Race savedRace = raceRepository.save(race);
        return mapToRaceDto(savedRace);
    }

    /**
     * Retrieves all races.
     *
     * @return list of all race DTOs.
     */
    public List<RaceDto> getAllRaces() {
        return raceRepository.findAll().stream().map(this::mapToRaceDto).toList();
    }

    /**
     * Retrieves a race by ID.
     *
     * @param id the ID of the race to retrieve.
     * @return Optional containing the race DTO, or empty if not found.
     */
    public Optional<RaceDto> getRaceById(Long id) {
        return raceRepository.findById(id).map(this::mapToRaceDto);
    }

    /**
     * Updates an existing race.
     *
     * @param id the ID of the race to update.
     * @param raceDto DTO containing updated race data.
     * @return Optional containing the updated race DTO, or empty if not found.
     */
    public Optional<RaceDto> updateRace(Long id, RaceDto raceDto) {
        return raceRepository.findById(id).map(race -> {
            race.setName(raceDto.getName());
            race.setYear(raceDto.getYear());

            Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(raceDto.getPilotIds()));
            race.getPilots().clear();
            race.getPilots().addAll(pilots);

            return mapToRaceDto(raceRepository.save(race));
        });
    }

    /**
     * Partially updates a race's fields.
     *
     * @param id      the ID of the race to update.
     * @param updates map containing fields to update.
     * @return optional of updated race DTO.
     */
    public Optional<RaceDto> partialUpdateRace(Long id, Map<String, Object> updates) {
        return raceRepository.findById(id).map(race -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> race.setName((String) value);
                    case "year" -> race.setYear((Integer) value);
                    case "pilotIds" -> {
                        List<Long> pilotIds = (List<Long>) value;
                        Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(pilotIds));
                        race.setPilots(pilots);
                    }
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }
            });
            return mapToRaceDto(raceRepository.save(race));
        });
    }

    /**
     * Deletes a race by ID.
     *
     * @param id the ID of the race to delete.
     */
    public void deleteRace(Long id) {
        raceRepository.deleteById(id);
    }
}