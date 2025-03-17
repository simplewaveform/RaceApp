package com.example.raceapp.service;

import com.example.raceapp.dto.CarResponse;
import com.example.raceapp.dto.PilotSimpleResponse;
import com.example.raceapp.dto.RaceDto;
import com.example.raceapp.dto.RaceResponse;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import com.example.raceapp.utils.CacheManager;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing race-related operations including creation, retrieval,
 * updating, and deletion of races. Handles participant associations and mapping
 * between entities and DTOs.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class RaceService {
    private final RaceRepository raceRepository;
    private final CarRepository carRepository;
    private final PilotRepository pilotRepository;
    private final PilotService pilotService;
    private final CarService carService;
    private final CacheManager cache;
    private static final String CACHE_PREFIX = "RACE_";

    /**
     * Clears the race-related cache.
     */
    private void clearRaceCache() {
        cache.evictByKeyPattern(CACHE_PREFIX);
    }

    /**
     * Maps a {@link Race} entity to a {@link RaceResponse} DTO.
     *
     * @param race The race entity to map.
     * @return A {@link RaceResponse} representing the mapped race.
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
                .map(this::mapToCarResponse)
                .collect(Collectors.toSet()));

        return response;
    }

    /**
     * Maps a {@link Car} entity to a {@link CarResponse} DTO.
     *
     * @param car The car entity to map.
     * @return A {@link CarResponse} representing the mapped car.
     */
    private CarResponse mapToCarResponse(Car car) {
        return getCarResponse(car);
    }

    /**
     * Static utility method to map a {@link Car} entity to a {@link CarResponse}.
     *
     * @param car The car entity to map.
     * @return A {@link CarResponse} representing the mapped car.
     */
    static CarResponse getCarResponse(Car car) {
        CarResponse response = new CarResponse();
        response.setId(car.getId());
        response.setBrand(car.getBrand());
        response.setModel(car.getModel());
        response.setPower(car.getPower());

        if (car.getOwner() != null) {
            PilotSimpleResponse owner = new PilotSimpleResponse();
            owner.setId(car.getOwner().getId());
            owner.setName(car.getOwner().getName());
            owner.setExperience(car.getOwner().getExperience());
            response.setOwner(owner);
        }
        return response;
    }

    /**
     * Creates a new race with associated pilots and cars.
     *
     * @param request DTO containing race details.
     * @return The created race as a {@link RaceResponse}.
     */
    public RaceResponse createRace(RaceDto request) {
        clearRaceCache();
        Race race = new Race();
        race.setName(request.getName());
        race.setYear(request.getYear());

        Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(request.getPilotIds()));
        race.setPilots(pilots);

        Set<Car> cars = new HashSet<>(carRepository.findAllById(request.getCarIds()));
        race.setCars(cars);

        return mapToResponse(raceRepository.save(race));
    }

    /**
     * Retrieves a paginated list of all races.
     *
     * @param pageable Pagination details.
     * @return A paginated list of {@link RaceResponse}.
     */
    public Page<RaceResponse> getAllRaces(Pageable pageable) {
        String cacheKey = String.format("%sALL_RACES_PAGE_%d_SIZE_%d", CACHE_PREFIX,
                pageable.getPageNumber(), pageable.getPageSize());
        Page<RaceResponse> cached = cache.get(cacheKey);

        if (cached != null) {
            return cached;
        }

        Page<RaceResponse> result = raceRepository.findAll(pageable).map(this::mapToResponse);

        cache.put(cacheKey, result);
        return result;
    }

    /**
     * Retrieves a race by its ID.
     *
     * @param id The ID of the race to retrieve.
     * @return An {@link Optional} containing the race if found, or empty otherwise.
     */
    public Optional<RaceResponse> getRaceById(Long id) {
        String cacheKey = CACHE_PREFIX + "RACE_ID_" + id;
        RaceResponse cached = cache.get(cacheKey);

        if (cached != null) {
            return Optional.of(cached);
        }

        Optional<RaceResponse> result = raceRepository.findById(id).map(this::mapToResponse);

        result.ifPresent(response -> cache.put(cacheKey, response));
        return result;
    }

    /**
     * Updates an existing race with new details.
     *
     * @param id The ID of the race to update.
     * @param request DTO containing the updated race details.
     * @return An {@link Optional} containing the updated race.
     */
    public Optional<RaceResponse> updateRace(Long id, RaceDto request) {
        clearRaceCache();
        return raceRepository.findById(id).map(race -> {
            race.setName(request.getName());
            race.setYear(request.getYear());

            Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(request.getPilotIds()));
            race.getPilots().clear();
            race.getPilots().addAll(pilots);

            Set<Car> cars = new HashSet<>(carRepository.findAllById(request.getCarIds()));
            race.getCars().clear();
            race.getCars().addAll(cars);

            pilotService.invalidateCache();
            carService.clearCarCache();

            return mapToResponse(raceRepository.save(race));
        });
    }

    /**
     * Partially updates a race's details based on provided key-value pairs.
     *
     * @param id The ID of the race to update.
     * @param updates A map of fields to update with their new values.
     * @return An {@link Optional} containing the updated race.
     */
    public Optional<RaceResponse> partialUpdateRace(Long id, Map<String, Object> updates) {
        pilotService.invalidateCache();
        carService.clearCarCache();
        clearRaceCache();
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
                    case "carIds" -> {
                        List<Long> carIds = (List<Long>) value;
                        Set<Car> cars = new HashSet<>(carRepository.findAllById(carIds));
                        race.setCars(cars);
                    }
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }

            });
            return mapToResponse(raceRepository.save(race));
        });
    }

    /**
     * Deletes a race by its ID.
     *
     * @param id The ID of the race to delete.
     */
    public void deleteRace(Long id) {
        clearRaceCache();
        Race race = raceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Race not found with id: " + id));

        race.getCars().clear();
        race.getPilots().clear();

        pilotService.invalidateCache();
        carService.clearCarCache();

        raceRepository.delete(race);
    }
}
