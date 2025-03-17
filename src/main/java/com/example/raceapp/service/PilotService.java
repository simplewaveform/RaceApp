package com.example.raceapp.service;

import com.example.raceapp.dto.CarSimpleResponse;
import com.example.raceapp.dto.PilotDto;
import com.example.raceapp.dto.PilotResponse;
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

    private CarSimpleResponse mapToCarSimpleResponse(Car car) {
        CarSimpleResponse response = new CarSimpleResponse();
        response.setId(car.getId());
        response.setBrand(car.getBrand());
        response.setModel(car.getModel());
        response.setPower(car.getPower());
        return response;
    }

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

    @Cacheable(value = "pilots", key = "{#brand, #pageable.pageNumber, #pageable.pageSize}")
    public Page<PilotResponse> getPilotsByCarBrandNative(String brand, Pageable pageable) {
        return pilotRepository.findPilotsByCarBrandNative(brand, pageable)
                .map(this::mapToResponse);
    }

    @Cacheable(value = "pilots", key = "{#name, #age, #experience, #pageable.pageNumber, #pageable.pageSize}")
    public Page<PilotResponse> searchPilotsWithPagination(
            String name,
            Integer age,
            Integer experience,
            Pageable pageable
    ) {
        Specification<Pilot> spec = (root, _, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null) predicates.add(cb.equal(root.get("name"), name));
            if (age != null) predicates.add(cb.equal(root.get("age"), age));
            if (experience != null) predicates.add(cb.equal(root.get("experience"), experience));
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return pilotRepository.findAll(spec, pageable).map(this::mapToResponse);
    }

    @Cacheable(value = "pilots", key = "#id")
    public Optional<PilotResponse> getPilotById(Long id) {
        return pilotRepository.findById(id).map(this::mapToResponse);
    }

    /**
     * Retrieves pilots by their IDs.
     */
    public Set<Pilot> getPilotsByIds(Set<Long> ids) {
        return new HashSet<>(pilotRepository.findAllById(ids));
    }

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
                    default -> throw new IllegalArgumentException("Invalid field: " + key);
                }
            });
            return mapToResponse(pilotRepository.save(pilot));
        });
    }

    @Caching(evict = {
            @CacheEvict(value = "pilots", allEntries = true),
            @CacheEvict(value = "cars", allEntries = true),
            @CacheEvict(value = "races", allEntries = true)
    })
    public void deletePilot(Long id) {
        Pilot pilot = pilotRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Pilot not found"));

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