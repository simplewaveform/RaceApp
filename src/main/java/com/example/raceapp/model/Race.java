package com.example.raceapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a race entity in the racing application.
 * This class is mapped to the "races" table in the database
 * ыand contains information about the race,
 * including its name, year, participating pilots, and participating cars.
 */
@Entity
@Table(name = "races")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
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
    @JsonIgnore
    private Set<Car> cars = new HashSet<>();

    /**
     * Adds a pilot to the set of pilots participating in this race.
     * Ensures that the relationship between the race and the pilot is properly maintained.
     *
     * @param pilot The pilot to be added to the race's pilot set.
     */
    public void addPilot(Pilot pilot) {
        this.pilots.add(pilot);
        pilot.getRaces().add(this);
    }

    /**
     * Removes a pilot from the set of pilots participating in this race.
     * Ensures that the relationship between the race and the pilot is properly removed.
     *
     * @param pilot The pilot to be removed from the race's pilot set.
     */
    public void removePilot(Pilot pilot) {
        this.pilots.remove(pilot);
        pilot.getRaces().remove(this);
    }

    /**
     * Adds a car to the set of cars participating in this race.
     * Ensures that the relationship between the race and the car is properly maintained.
     *
     * @param car The car to be added to the race's car set.
     */
    public void addCar(Car car) {
        this.cars.add(car);
        car.getRaces().add(this);
    }

    /**
     * Removes a car from the set of cars participating in this race.
     * Ensures that the relationship between the race and the car is properly removed.
     *
     * @param car The car to be removed from the race's car set.
     */
    public void removeCar(Car car) {
        this.cars.remove(car);
        car.getRaces().remove(this);
    }

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