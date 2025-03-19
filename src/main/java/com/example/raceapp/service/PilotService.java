package com.example.raceapp.service;

import com.example.raceapp.dto.CarSimpleResponse;
import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
import com.example.raceapp.exception.BadRequestException;
import com.example.raceapp.exception.NotFoundException;
import com.example.raceapp.exception.ValidationException;
import com.example.raceapp.model.Car;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.PilotRepository;
import com.example.raceapp.repository.RaceRepository;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for managing pilot-related operations including creation,
 * retrieval, updating, and deletion of pilots.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PilotService {
    private final PilotRepository pilotRepository;
    private final RaceRepository raceRepository;

    /**
     * Maps a {@link Pilot} entity to a {@link PilotResponse} DTO.
     *
     * @param pilot the pilot entity to map
     * @return a {@link PilotResponse} containing the mapped pilot details
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
     * @param car the car entity to map
     * @return a {@link CarSimpleResponse} containing the mapped car details
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
     * Creates a new pilot from the provided {@link PilotDto}.
     *
     * @param request the DTO containing the new pilot's details
     * @return the created pilot's response as a {@link PilotResponse}
     */
    @Caching(evict = {
        @CacheEvict(value = "pilots", allEntries = true),
        @CacheEvict(value = "cars", allEntries = true)
    })
    public PilotResponse createPilot(PilotDto request) {
        Pilot pilot = new Pilot();
        pilot.setName(request.getName());
        pilot.setAge(request.getAge());
        pilot.setExperience(request.getExperience());
        return mapToResponse(pilotRepository.save(pilot));
    }

    /**
     * Retrieves a paginated list of pilots who own cars of a specific brand.
     *
     * @param brand    the car brand to filter by
     * @param pageable the pagination details
     * @return a paginated list of {@link PilotResponse} objects
     */
    @Cacheable(value = "pilots", key = "{#brand, #pageable.pageNumber, #pageable.pageSize}")
    public Page<PilotResponse> getPilotsByCarBrandNative(String brand, Pageable pageable) {
        return pilotRepository.findPilotsByCarBrandNative(brand, pageable)
                .map(this::mapToResponse);
    }

    /**
     * Searches for pilots based on provided filters (name, age, experience)
     * with pagination support.
     *
     * @param name       the name to search by
     * @param age        the age to search by
     * @param experience the experience level to search by
     * @param pageable   the pagination details
     * @return a paginated list of {@link PilotResponse} objects that match the search criteria
     */
    @Cacheable(value = "pilots", key = "{#name, #age, #experience, "
            + "#pageable.pageNumber, #pageable.pageSize}")
    public Page<PilotResponse> searchPilotsWithPagination(
            String name,
            Integer age,
            Integer experience,
            Pageable pageable
    ) {
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

        return pilotRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    /**
     * Retrieves a pilot by their ID.
     *
     * @param id the ID of the pilot to retrieve
     * @return an {@link Optional} containing the {@link PilotResponse}
     *          if found, or empty if not found
     */
    @Cacheable(value = "pilots", key = "#id")
    public Optional<PilotResponse> getPilotById(Long id) {
        return pilotRepository.findById(id).map(this::mapToResponse);
    }

    /**
     * Retrieves pilots by their IDs.
     *
     * @param ids a set of pilot IDs
     * @return a set of {@link Pilot} entities
     */
    public Set<Pilot> getPilotsByIds(Set<Long> ids) {
        return new HashSet<>(pilotRepository.findAllById(ids));
    }

    /**
     * Updates an existing pilot with the details from the provided {@link PilotDto}.
     *
     * @param id      the ID of the pilot to update
     * @param request the DTO containing the updated details
     * @return an {@link Optional} containing the updated {@link PilotResponse} if successful
     */
    @Caching(evict = {
        @CacheEvict(value = "pilots", allEntries = true),
        @CacheEvict(value = "cars", allEntries = true)
    })
    public Optional<PilotResponse> updatePilot(Long id, PilotDto request) {
        return pilotRepository.findById(id).map(pilot -> {
            pilot.setName(request.getName());
            pilot.setAge(request.getAge());
            pilot.setExperience(request.getExperience());
            return mapToResponse(pilotRepository.save(pilot));
        });
    }

    /**
     * Partially updates a pilot with the fields provided in the {@link Map} of updates.
     *
     * @param id      the ID of the pilot to update
     * @param updates a map of fields to update with their new values
     * @return an {@link Optional} containing the updated {@link PilotResponse} if successful
     */
    @Caching(evict = {
        @CacheEvict(value = "pilots", allEntries = true),
        @CacheEvict(value = "cars", allEntries = true)
    })
    public Optional<PilotResponse> partialUpdatePilot(Long id, Map<String, Object> updates) {
        return pilotRepository.findById(id).map(pilot -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> pilot.setName((String) value);
                    case "age" -> pilot.setAge((Integer) value);
                    case "experience" -> pilot.setExperience((Integer) value);
                    default -> throw new ValidationException(Map.of(key, "Invalid field: "
                            + key));

                }
            });
            return mapToResponse(pilotRepository.save(pilot));
        });
    }

    /**
     * Deletes a pilot by their ID and removes them from any associated cars and races.
     *
     * @param id the ID of the pilot to delete
     */
    @Caching(evict = {
        @CacheEvict(value = "pilots", allEntries = true),
        @CacheEvict(value = "cars", allEntries = true),
        @CacheEvict(value = "races", allEntries = true)
    })
    public void deletePilot(Long id) {
        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pilot not found"));

        pilot.getCars().forEach(car -> {
            car.setOwner(null);
            car.getRaces().forEach(race -> {
                race.getCars().remove(car);
                raceRepository.save(race);
            });
        });

        pilot.getRaces().forEach(race -> {
            race.getPilots().remove(pilot);
            raceRepository.save(race);
        });

        pilotRepository.delete(pilot);
    }
}