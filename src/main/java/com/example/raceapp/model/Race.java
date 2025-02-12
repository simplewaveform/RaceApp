package com.example.raceapp.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "races")
public class Race {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int year;

    @ManyToMany
    @JoinTable(
            name = "race_pilot",
            joinColumns = @JoinColumn(name = "race_id"),
            inverseJoinColumns = @JoinColumn(name = "pilot_id")
    )
    private Set<Pilot> pilots = new HashSet<>();

    @ManyToMany(mappedBy = "races")
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

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public Set<Pilot> getPilots() {
        return pilots;
    }

    public void setPilots(Set<Pilot> pilots) {
        this.pilots = pilots;
    }
}
