package com.example.raceapp.service;

import com.example.raceapp.dto.RaceDTO;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service for managing races.
 */
@Service
@Transactional
public class RaceService {
    private final RaceRepository raceRepository;
    private final PilotRepository pilotRepository;

    public RaceService(RaceRepository raceRepository, PilotRepository pilotRepository) {
        this.raceRepository = raceRepository;
        this.pilotRepository = pilotRepository;
    }

    /**
     * Creates a new race.
     * @param raceDTO DTO with race data.
     * @return Created race DTO.
     */
    public RaceDTO createRace(RaceDTO raceDTO) {
        Race race = new Race();
        race.setName(raceDTO.getName());
        race.setYear(raceDTO.getYear());

        Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(raceDTO.getPilotIds()));
        race.setPilots(pilots);

        Race savedRace = raceRepository.save(race);
        return mapToRaceDTO(savedRace);
    }

    /**
     * Retrieves all races.
     * @return List of all races.
     */
    public List<RaceDTO> getAllRaces() {
        return raceRepository.findAll().stream()
                .map(this::mapToRaceDTO)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves a race by ID.
     * @param id Race ID.
     * @return Optional containing race DTO.
     */
    public Optional<RaceDTO> getRaceById(Long id) {
        return raceRepository.findById(id).map(this::mapToRaceDTO);
    }

    /**
     * Updates a race.
     * @param id Race ID.
     * @param raceDTO Updated data.
     * @return Optional containing updated race DTO.
     */
    public Optional<RaceDTO> updateRace(Long id, RaceDTO raceDTO) {
        return raceRepository.findById(id).map(race -> {
            race.setName(raceDTO.getName());
            race.setYear(raceDTO.getYear());

            Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(raceDTO.getPilotIds()));
            race.getPilots().retainAll(pilots);
            race.getPilots().addAll(pilots);

            return mapToRaceDTO(raceRepository.save(race));
        });
    }

    /**
     * Deletes a race by ID.
     * @param id Race ID.
     */
    public void deleteRace(Long id) {
        raceRepository.deleteById(id);
    }

    private RaceDTO mapToRaceDTO(Race race) {
        RaceDTO raceDTO = new RaceDTO();
        raceDTO.setId(race.getId());
        raceDTO.setName(race.getName());
        raceDTO.setYear(race.getYear());
        raceDTO.setPilotIds(race.getPilots().stream()
                .map(Pilot::getId)
                .collect(Collectors.toSet()));
        return raceDTO;
    }
}