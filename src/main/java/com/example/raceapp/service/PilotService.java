package com.example.raceapp.service;

import com.example.raceapp.dto.CarSimpleResponse;
import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import com.example.raceapp.utils.CacheManager;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing pilot-related operations including creation,
 * retrieval, updating, and deletion of pilots.
 * Handles caching, data mapping, and database interactions.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PilotService {
    private final PilotRepository pilotRepository;
    private final RaceRepository raceRepository;
    private final CarService carService;
    private final CacheManager cache;
    private static final String CACHE_PREFIX = "PILOT_";

    /**
     * Invalidates all cache entries related to pilots.
     */
    void invalidateCache() {
        cache.evictByKeyPattern(CACHE_PREFIX);
    }

    /**
     * Maps a {@link Pilot} entity to a {@link PilotResponse} DTO.
     *
     * @param pilot The pilot entity to map.
     * @return A {@link PilotResponse} representing the mapped pilot.
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
     * Maps a {@link Car} entity to a {@link CarSimpleResponse} DTO.
     *
     * @param car The car entity to map.
     * @return A {@link CarSimpleResponse} representing the mapped car.
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
     * Creates a new pilot.
     *
     * @param request The DTO containing pilot details.
     * @return The created pilot as a {@link PilotResponse}.
     */
    public PilotResponse createPilot(PilotDto request) {
        invalidateCache();

        Pilot pilot = new Pilot();
        pilot.setName(request.getName());
        pilot.setAge(request.getAge());
        pilot.setExperience(request.getExperience());
        return mapToResponse(pilotRepository.save(pilot));
    }

    /**
     * Searches for pilots based on given criteria.
     *
     * @param name The pilot's name.
     * @param age The pilot's age.
     * @param experience The pilot's experience.
     * @return A list of pilots matching the given criteria.
     */
    public Page<PilotResponse> searchPilotsWithPagination(
            String name,
            Integer age,
            Integer experience,
            Pageable pageable
    ) {
        String cacheKey = String.format(
                "%sSEARCH_%s_%d_%d_PAGE_%d_SIZE_%d",
                CACHE_PREFIX,
                name, age, experience,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<PilotResponse> cached = cache.get(cacheKey);
        if (cached != null) {
            return cached;
        }

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

        Page<Pilot> pilots = pilotRepository.findAll(spec, pageable);
        Page<PilotResponse> result = pilots.map(this::mapToResponse);
        cache.put(cacheKey, result);
        return result;
    }

    /**
     * Retrieves a paginated list of pilots who own a car of the specified brand.
     *
     * @param brand The car's brand.
     * @param pageable Pagination details.
     * @return A paginated list of pilots.
     */
    public Page<PilotResponse> getPilotsByCarBrand(String brand, Pageable pageable) {
        String cacheKey = String.format("%sBY_BRAND_%s_PAGE_%d_SIZE_%d", CACHE_PREFIX, brand,
                pageable.getPageNumber(), pageable.getPageSize());
        Page<PilotResponse> cached = cache.get(cacheKey);

        if (cached != null) {
            return cached;
        }

        Page<PilotResponse> result = pilotRepository.findPilotsByCarBrand(brand, pageable)
                .map(this::mapToResponse);

        cache.put(cacheKey, result);
        return result;
    }

    /**
     * Retrieves a pilot by its ID.
     *
     * @param id The ID of the pilot.
     * @return An {@link Optional} containing the pilot if found.
     */
    public Optional<PilotResponse> getPilotById(Long id) {
        return pilotRepository.findById(id).map(this::mapToResponse);
    }

    /**
     * Updates a pilot's details.
     *
     * @param id The ID of the pilot to update.
     * @param request The DTO containing updated pilot data.
     * @return The updated pilot wrapped in an {@link Optional}.
     */
    public Optional<PilotResponse> updatePilot(Long id, PilotDto request) {
        carService.clearCarCache();
        invalidateCache();
        return pilotRepository.findById(id).map(pilot -> {
            pilot.setName(request.getName());
            pilot.setAge(request.getAge());
            pilot.setExperience(request.getExperience());
            return mapToResponse(pilotRepository.save(pilot));
        });
    }

    /**
     * Partially updates a pilot's details.
     *
     * @param id The ID of the pilot to update.
     * @param updates A map of fields to update with their new values.
     * @return The updated pilot wrapped in an {@link Optional}.
     */
    public Optional<PilotResponse> partialUpdatePilot(Long id, Map<String, Object> updates) {
        carService.clearCarCache();
        invalidateCache();
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
     * Deletes a pilot by their ID and updates related races and cars accordingly.
     *
     * @param id The ID of the pilot to delete.
     */
    public void deletePilot(Long id) {
        invalidateCache();
        carService.clearCarCache();

        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pilot not found with id: " + id));

        for (Car car : pilot.getCars()) {
            for (Race race : car.getRaces()) {
                race.getCars().remove(car);
                raceRepository.save(race);
            }
            car.setOwner(null);
        }

        for (Race race : pilot.getRaces()) {
            race.getPilots().remove(pilot);
            raceRepository.save(race);
        }

        pilotRepository.delete(pilot);
    }
}
