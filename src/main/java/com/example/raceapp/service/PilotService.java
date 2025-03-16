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
 * Service for managing pilot-related operations including creation, retrieval,
 * updating, and deletion of pilots. Handles mapping between entities and DTOs.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PilotService {
    private final PilotRepository pilotRepository;
    private final RaceRepository raceRepository;
    private final CacheManager cache;
    private static final String CACHE_PREFIX = "PILOT_";

    private void invalidateCache() {
        cache.evictByKeyPattern(CACHE_PREFIX);
    }

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

    public PilotResponse createPilot(PilotDto request) {
        invalidateCache();

        Pilot pilot = new Pilot();
        pilot.setName(request.getName());
        pilot.setAge(request.getAge());
        pilot.setExperience(request.getExperience());
        return mapToResponse(pilotRepository.save(pilot));
    }

    public List<PilotResponse> searchPilots(String name, Integer age, Integer experience) {
        String cacheKey = String.format("%sSEARCH_%s_%s_%s", CACHE_PREFIX, name, age, experience);
        List<PilotResponse> cached = cache.get(cacheKey);
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

        List<PilotResponse> result = pilotRepository.findAll(spec).stream()
                .map(this::mapToResponse)
                .toList();

        cache.put(cacheKey, result);
        return result;
    }

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

    public Optional<PilotResponse> getPilotById(Long id) {
        return pilotRepository.findById(id).map(this::mapToResponse);
    }

    public Optional<PilotResponse> updatePilot(Long id, PilotDto request) {
        invalidateCache();
        return pilotRepository.findById(id).map(pilot -> {
            pilot.setName(request.getName());
            pilot.setAge(request.getAge());
            pilot.setExperience(request.getExperience());
            return mapToResponse(pilotRepository.save(pilot));
        });
    }

    public Optional<PilotResponse> partialUpdatePilot(Long id, Map<String, Object> updates) {
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

    public void deletePilot(Long id) {
        invalidateCache();

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
