package com.example.raceapp.service;

import com.example.raceapp.dto.*;
import com.example.raceapp.model.Race;
import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Car;
import com.example.raceapp.repository.CarRepository;
import com.example.raceapp.repository.RaceRepository;
import com.example.raceapp.repository.PilotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    /**
     * Maps a Race entity to a RaceResponse DTO.
     *
     * @param race the Race entity to convert
     * @return RaceResponse containing race details and full participant data
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
     * Maps a Car entity to a CarResponse DTO.
     *
     * @param car the Car entity to convert
     * @return CarResponse containing car details and owner information
     */
    private CarResponse mapToCarResponse(Car car) {
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
     * Creates a new race from the provided request data.
     *
     * @param request the RaceRequest containing race details
     * @return RaceResponse with the created race's details
     */
    public RaceResponse createRace(RaceDto request) {
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
     * Retrieves all races with their associated pilots and cars.
     *
     * @return List of RaceResponse containing all races
     */
    public List<RaceResponse> getAllRaces() {
        return raceRepository.findAll().stream()
                .map(this::mapToResponse)
                .toList();
    }

    /**
     * Retrieves a race by ID.
     *
     * @param id the ID of the race to retrieve
     * @return Optional containing RaceResponse if found, empty otherwise
     */
    public Optional<RaceResponse> getRaceById(Long id) {
        return raceRepository.findById(id)
                .map(this::mapToResponse);
    }

    /**
     * Updates an existing race with new data.
     *
     * @param id the ID of the race to update
     * @param request the RaceRequest containing updated data
     * @return Optional containing updated RaceResponse if found, empty otherwise
     */
    public Optional<RaceResponse> updateRace(Long id, RaceDto request) {
        return raceRepository.findById(id).map(race -> {
            race.setName(request.getName());
            race.setYear(request.getYear());

            Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(request.getPilotIds()));
            race.getPilots().clear();
            race.getPilots().addAll(pilots);

            return mapToResponse(raceRepository.save(race));
        });
    }

    /**
     * Partially updates specific fields of a race.
     *
     * @param id the ID of the race to update
     * @param updates map containing fields to update
     * @return Optional containing updated RaceResponse if found, empty otherwise
     * @throws IllegalArgumentException if invalid field is provided
     */
    public Optional<RaceResponse> partialUpdateRace(Long id, Map<String, Object> updates) {
        return raceRepository.findById(id).map(race -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> race.setName((String) value);
                    case "year" -> race.setYear((Integer) value);
                    case "pilotIds" -> {
                        Set<Long> pilotIds = (Set<Long>) value;
                        Set<Pilot> pilots = new HashSet<>(pilotRepository.findAllById(pilotIds));
                        race.setPilots(pilots);
                    }
                    case "carIds" -> {
                        Set<Long> carIds = new HashSet<>((List<Long>) value);
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
     * Deletes a race by ID.
     *
     * @param id the ID of the race to delete
     */
    public void deleteRace(Long id) {
        Race race = raceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Race not found with id: " + id));

        race.getCars().clear();
        race.getPilots().clear();

        raceRepository.delete(race);
    }
}