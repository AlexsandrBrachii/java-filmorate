package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class Film {

  private Integer id;
  private final String name;
  private final String description;
  private final LocalDate releaseDate;
  private final Integer duration;
  private final Integer rate;
  private Mpa mpa;
  private List<Genre> genres;
  @JsonIgnore
  private Set<Integer> likes;
  private List<Director> directors = new ArrayList<>();
}
