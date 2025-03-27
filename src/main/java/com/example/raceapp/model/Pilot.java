package com.example.raceapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a pilot (driver) entity in the system.
 */
@Entity
@Table(name = "pilots")
public class Pilot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;
    private Integer experience;

    @OneToMany(mappedBy = "owner", orphanRemoval = true)
    private Set<Car> cars = new HashSet<>();

    @ManyToMany(mappedBy = "pilots")
    private Set<Race> races = new HashSet<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public Set<Car> getCars() { return cars; }
    public void setCars(Set<Car> cars) { this.cars = cars; }

    public Set<Race> getRaces() { return races; }
    public void setRaces(Set<Race> races) { this.races = races; }
}
