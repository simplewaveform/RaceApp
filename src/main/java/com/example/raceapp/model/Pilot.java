package com.example.raceapp.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a pilot.
 * This class stores information about the pilot such as name, age, experience,
 * the cars they own, and the races they participate in.
 */
@Entity
@Table(name = "pilots")
public class Pilot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int age;
    private int experience;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Car> cars = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "race_pilot",
            joinColumns = @JoinColumn(name = "pilot_id"),
            inverseJoinColumns = @JoinColumn(name = "race_id")
    )
    private Set<Race> races = new HashSet<>();

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public Set<Race> getRaces() {
        return races;
    }

    public void setRaces(Set<Race> races) {
        this.races = races;
    }
}
