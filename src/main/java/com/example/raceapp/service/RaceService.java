package com.example.raceapp.service;

import com.example.raceapp.dto.PilotSimpleResponse;
import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.dto.RaceResponse;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.RaceRepository;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing race-related operations including creation,
 * retrieval, updating, and deletion of races.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RaceService {

    private final RaceRepository raceRepository;
    private final PilotService pilotService;
    private final CarService carService;

    /**
     * Maps a {@link Pilot} entity to a {@link PilotSimpleResponse} DTO.
     *
     * @param pilot the pilot entity
     * @return the mapped PilotSimpleResponse
     */
    static PilotSimpleResponse mapToPilotSimpleResponse(Pilot pilot) {
        PilotSimpleResponse response = new PilotSimpleResponse();
        response.setId(pilot.getId());
        response.setName(pilot.getName());
        response.setExperience(pilot.getExperience());
        return response;
    }

    /**
     * Maps a {@link Race} entity to a {@link RaceResponse} DTO.
     *
     * @param race the race entity
     * @return the mapped RaceResponse DTO
     */
    private RaceResponse mapToResponse(Race race) {
        RaceResponse response = new RaceResponse();
        response.setId(race.getId());
        response.setName(race.getName());
        response.setYear(race.getYear());
        response.setPilots(race.getPilots().stream()
                .map(pilotService::mapToResponse)
                .collect(Collectors.toSet()));
        response.setCars(race.getCars().stream()
                .map(carService::mapToResponse)
                .collect(Collectors.toSet()));
        return response;
    }

    /**
     * Creates a new race from the provided {@link RaceDto} request.
     *
     * @param request the RaceDto containing race data
     * @return the created RaceResponse DTO
     */
    @Caching(evict = {
        @CacheEvict(value = "races", allEntries = true),
        @CacheEvict(value = "pilots", allEntries = true),
        @CacheEvict(value = "cars", allEntries = true)
    })
    public RaceResponse createRace(RaceDto request) {
        Race race = new Race();
        race.setName(request.getName());
        race.setYear(request.getYear());
        race.setPilots(pilotService.getPilotsByIds(request.getPilotIds()));
        race.setCars(carService.getCarsByIds(request.getCarIds()));
        return mapToResponse(raceRepository.save(race));
    }

    /**
     * Retrieves all races with pagination.
     *
     * @param pageable the pagination information
     * @return a page of RaceResponse DTOs
     */
    @Cacheable(value = "races", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<RaceResponse> getAllRaces(Pageable pageable) {
        return raceRepository.findAll(pageable).map(this::mapToResponse);
    }

    /**
     * Retrieves a race by its ID.
     *
     * @param id the race ID
     * @return an Optional containing the RaceResponse DTO if found
     */
    @Cacheable(value = "races", key = "#id")
    public Optional<RaceResponse> getRaceById(Long id) {
        return raceRepository.findById(id).map(this::mapToResponse);
    }

    /**
     * Updates an existing race identified by its ID using the provided {@link RaceDto} request.
     *
     * @param id the race ID
     * @param request the RaceDto containing updated race data
     * @return an Optional containing the updated RaceResponse DTO if the race was found
     */
    @Caching(evict = {
        @CacheEvict(value = "races", allEntries = true),
        @CacheEvict(value = "pilots", allEntries = true),
        @CacheEvict(value = "cars", allEntries = true)
    })
    public Optional<RaceResponse> updateRace(Long id, RaceDto request) {
        return raceRepository.findById(id).map(race -> {
            race.setName(request.getName());
            race.setYear(request.getYear());
            race.setPilots(pilotService.getPilotsByIds(request.getPilotIds()));
            race.setCars(carService.getCarsByIds(request.getCarIds()));
            return mapToResponse(raceRepository.save(race));
        });
    }

    /**
     * Deletes a race identified by its ID.
     *
     * @param id the race ID
     * @throws IllegalArgumentException if the race with the specified ID is not found
     */
    @Caching(evict = {
        @CacheEvict(value = "races", allEntries = true),
        @CacheEvict(value = "pilots", allEntries = true),
        @CacheEvict(value = "cars", allEntries = true)
    })
    public void deleteRace(Long id) {
        Race race = raceRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Race not found"));
        race.getPilots().clear();
        race.getCars().clear();
        raceRepository.delete(race);
    }
}
