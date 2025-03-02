package com.example.raceapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a pilot entity in the racing application.
 * This class is mapped to the "pilots" table in the
 * database and contains information about the pilot,
 * including their name, age, experience, owned cars, and participated races.
 */
@Entity
@Table(name = "pilots")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Pilot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private Integer age;
    private Integer experience;

    @OneToMany(mappedBy = "owner", cascade = {CascadeType.PERSIST,
                                              CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
    private Set<Car> cars = new HashSet<>();

    @ManyToMany(mappedBy = "pilots", fetch = FetchType.LAZY)
    @JsonIgnore
    private Set<Race> races = new HashSet<>();

    /**
     * Default constructor for the Pilot entity.
     * Required by JPA for creating instances of this class.
     */
    public Pilot() {
    }

    /**
     * Adds a car to the set of cars owned by this pilot.
     * Ensures that the relationship between the pilot and the car is properly maintained.
     *
     * @param car The car to be added to the pilot's car set.
     */
    public void addCar(Car car) {
        this.cars.add(car);
        car.setOwner(this);
    }

    /**
     * Removes a car from the set of cars owned by this pilot.
     * Ensures that the relationship between the pilot and the car is properly removed.
     *
     * @param car The car to be removed from the pilot's car set.
     */
    public void removeCar(Car car) {
        this.cars.remove(car);
        car.setOwner(null);
    }

    /**
     * Adds a race to the set of races this pilot participates in.
     * Ensures that the relationship between the pilot and the race is properly maintained.
     *
     * @param race The race to be added to the pilot's race set.
     */
    public void addRace(Race race) {
        this.races.add(race);
        race.getPilots().add(this);
    }

    /**
     * Removes a race from the set of races this pilot participates in.
     * Ensures that the relationship between the pilot and the race is properly removed.
     *
     * @param race The race to be removed from the pilot's race set.
     */
    public void removeRace(Race race) {
        this.races.remove(race);
        race.getPilots().remove(this);
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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getExperience() {
        return experience;
    }

    public void setExperience(Integer experience) {
        this.experience = experience;
    }

    public Set<Car> getCars() {
        return cars;
    }

    public void setCars(Set<Car> cars) {
        this.cars = cars;
    }

    public Set<Race> getRaces() {
        return races;
    }

    public void setRaces(Set<Race> races) {
        this.races = races;
    }
}