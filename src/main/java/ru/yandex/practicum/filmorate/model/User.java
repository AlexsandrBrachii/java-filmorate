package ru.yandex.practicum.filmorate.model;


import java.time.LocalDate;

import lombok.Data;
import lombok.Builder;


@Builder
@Data
public class User {


    private Integer id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;


}


