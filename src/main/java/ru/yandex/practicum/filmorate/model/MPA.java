package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Builder
@Jacksonized
public class MPA {
    private Integer id;
    private String name;

    public MPA(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}