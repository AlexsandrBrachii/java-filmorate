package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class Film {

    private Integer id;
    private final String name;
    private final String description;
    private final LocalDate releaseDate;
    private final Integer duration;
    private final Integer rate;
    private MPA mpa;
    private List<Genre> genres;
    private Set<Integer> likes;
}