package com.example.raceapp.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a car entity in the racing application.
 * This class is mapped to the "cars" table in the database and contains information about the car,
 * including its brand, model, power, owner (Pilot), and the races it participates in.
 */
@Entity
@Table(name = "cars")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String brand;
    private String model;
    private int power;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pilot_id")
    @JsonIgnore
    private Pilot owner;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "race_car",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "race_id")
    )
    @JsonIgnore
    private Set<Race> races = new HashSet<>();

    /**
     * Default constructor for the Car entity.
     * Required by JPA for creating instances of this class.
     */
    public Car() {
    }

    /**
     * Adds a race to the set of races this car participates in.
     * Ensures that the relationship between the car and the race is properly maintained.
     *
     * @param race The race to be added to the car's race set.
     */
    public void addRace(Race race) {
        this.races.add(race);
        race.getCars().add(this);
    }

    /**
     * Removes a race from the set of races this car participates in.
     * Ensures that the relationship between the car and the race is properly removed.
     *
     * @param race The race to be removed from the car's race set.
     */
    public void removeRace(Race race) {
        this.races.remove(race);
        race.getCars().remove(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getPower() {
        return power;
    }

    public void setPower(int power) {
        this.power = power;
    }

    public Pilot getOwner() {
        return owner;
    }

    public void setOwner(Pilot owner) {
        this.owner = owner;
    }

    public Set<Race> getRaces() {
        return races;
    }

    public void setRaces(Set<Race> races) {
        this.races = races;
    }
}