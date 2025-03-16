package com.example.raceapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a pilot (driver) entity in the system.
 */
@Getter
@Setter
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Pilot pilot = (Pilot) o;
        return id != null && id.equals(pilot.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
