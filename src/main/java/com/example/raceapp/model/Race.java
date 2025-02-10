package com.example.raceapp.model;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Race {
    private int id;
    private String name;
    private int year;

    public Race(int id, String name, int year) {
        this.id = id;
        this.name = name;
        this.year = year;
    }

}
