package com.example.raceapp.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
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
import lombok.Data;

/**
 * Represents a pilot (driver) entity in the system.
 */
@Data
@Entity
@Table(name = "pilots")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id")
public class Pilot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;
    private Integer experience;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL,
               fetch = FetchType.LAZY, orphanRemoval = true)
    private Set<Car> cars = new HashSet<>();

    @ManyToMany(mappedBy = "pilots", fetch = FetchType.LAZY)
    private Set<Race> races = new HashSet<>();
}