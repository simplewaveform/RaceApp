package com.example.raceapp.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a race event in the system.
 */
@Entity
@Table(name = "races")
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer year;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "race_pilot",
            joinColumns = @JoinColumn(name = "race_id"),
            inverseJoinColumns = @JoinColumn(name = "pilot_id")
    )
    @JsonManagedReference("race-pilots")
    private Set<Pilot> pilots = new HashSet<>();

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "race_car",
            joinColumns = @JoinColumn(name = "race_id"),
            inverseJoinColumns = @JoinColumn(name = "car_id")
    )
    private Set<Car> cars = new HashSet<>();

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Set<Pilot> getPilots() {
        return pilots;
    }

    public void setPilots(Set<Pilot> pilots) {
        this.pilots = pilots;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }
}