package ru.yandex.practicum.filmorate.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private Integer id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    private Set<Integer> friends = new HashSet<>();
}