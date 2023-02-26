package ru.yandex.practicum.filmorate.model;


import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import lombok.Data;
import lombok.Builder;




@Data
public class User {

    private Integer id;
    private final String email;
    private final String login;
    private String name;
    private final LocalDate birthday;
    private Set<Long> friends = new HashSet<>();

}

/*
{
        "email": "test@example.com",
        "login": "testuser",
        "name": "Test User1",
        "birthday": "2000-01-01"
        }*/
