package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class Film {

    private Integer id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Integer duration;
    private Set<Long> likes = new HashSet<>();

}

       /*{
        "name": "Some Film Name",
        "description": "Some Film Description",
        "releaseDate": "2022-01-01",
        "duration": 120
        }*/