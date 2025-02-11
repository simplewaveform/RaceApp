package com.example.raceapp.service;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.repository.PilotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PilotService {

    private final PilotRepository pilotRepository;

    @Autowired
    public PilotService(PilotRepository pilotRepository) {
        this.pilotRepository = pilotRepository;
    }

    // 1. Добавление пилота в базу данных
    public Pilot createPilot(Pilot pilot) {
        return pilotRepository.save(pilot);
    }

    // 2. Получение всех пилотов
    public List<Pilot> getAllPilots() {
        return pilotRepository.findAll();
    }

    // 3. Получение пилота по ID
    public Pilot getPilotById(Long id) {
        return pilotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pilot not found with id: " + id));
    }

    // 4. Удаление пилота по ID
    public void deletePilot(Long id) {
        pilotRepository.deleteById(id);
    }
}
