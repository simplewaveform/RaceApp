package com.example.raceapp.service;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.PilotRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PilotService {
    private final PilotRepository pilotRepository;

    public PilotService(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    public Pilot createPilot(Pilot pilot) {
        return pilotRepository.save(pilot);
    }

    public Optional<Pilot> getPilotById(Long id) {
        return pilotRepository.findByIdWithCars(id);
    }

    public List<Pilot> searchPilots(String name, Integer age, Integer experience) {
        if (name != null || age != null || experience != null) {
            return pilotRepository.findByNameOrAgeOrExperience(name, age, experience);
        }
        return pilotRepository.findAll();
    }

    public Optional<Pilot> updatePilot(Long id, Pilot pilotDetails) {
        return pilotRepository.findById(id).map(pilot -> {
            pilot.setName(pilotDetails.getName());
            pilot.setAge(pilotDetails.getAge());
            pilot.setExperience(pilotDetails.getExperience());
            return pilotRepository.save(pilot);
        });
    }

    public Optional<Pilot> partialUpdatePilot(Long id, Pilot pilotDetails) {
        return pilotRepository.findById(id).map(pilot -> {
            if (pilotDetails.getName() != null) {
                pilot.setName(pilotDetails.getName());
            }
            if (pilotDetails.getAge() != null) {
                pilot.setAge(pilotDetails.getAge());
            }
            if (pilotDetails.getExperience() != null) {
                pilot.setExperience(pilotDetails.getExperience());
            }
            return pilotRepository.save(pilot);
        });
    }

    public void deletePilot(Long id) {
        pilotRepository.deleteById(id);
    }
}