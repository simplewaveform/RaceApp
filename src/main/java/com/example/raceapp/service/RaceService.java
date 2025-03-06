package com.example.raceapp.service;

import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import java.util.HashSet;
import java.util.List;
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
     * Creates a new race.
     *
     * @param raceDto Dto with race data.
     * @return Created race Dto.
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
     * @return List of all races.
     */
    public List<RaceDto> getAllRaces() {
        return raceRepository.findAll().stream()
                .map(this::mapToRaceDto)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a race by ID.
     *
     * @param id Race ID.
     * @return Optional containing race Dto.
     */
    public Optional<RaceDto> getRaceById(Long id) {
        return raceRepository.findById(id).map(this::mapToRaceDto);
    }

    /**
     * Updates a race.
     *
     * @param id Race ID.
     * @param raceDto Updated data.
     * @return Optional containing updated race Dto.
     */
    public Optional<RaceDto> updateRace(Long id, RaceDto raceDto) {
        return raceRepository.findById(id).map(race -> {
            race.setName(raceDto.getName());
            race.setYear(raceDto.getYear());

            Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(raceDto.getPilotIds()));
            race.getPilots().retainAll(pilots);
            race.getPilots().addAll(pilots);

            return mapToRaceDto(raceRepository.save(race));
        });
    }

    /**
     * Deletes a race by ID.
     *
     * @param id Race ID.
     */
    public void deleteRace(Long id) {
        raceRepository.deleteById(id);
    }

    private RaceDto mapToRaceDto(Race race) {
        RaceDto raceDto = new RaceDto();
        raceDto.setId(race.getId());
        raceDto.setName(race.getName());
        raceDto.setYear(race.getYear());
        raceDto.setPilotIds(race.getPilots().stream()
                .map(Pilot::getId)
                .collect(Collectors.toSet()));
        return raceDto;
    }
}