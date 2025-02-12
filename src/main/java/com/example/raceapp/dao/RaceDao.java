package com.example.raceapp.dao;

import com.example.raceapp.model.Pilot;
import com.example.raceapp.model.Race;
import java.util.List;
import java.util.Optional;

public interface RaceDao {
    Race save(Race race); // <- Метод сохранения гонки
    Optional<Race> findById(Long id);
    List<Race> findAll();
    void deleteById(Long id);
    List<Pilot> getPilotsForRace(Long raceId); // <- Метод получения пилотов для гонки
}
