package ru.yandex.practicum.filmorate.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;
import javax.validation.constraints.NotNull;


@Data
@Builder
@Jacksonized
public class Review {

    private Integer reviewId;
    @NotNull
    private String content;
    @NotNull
    @JsonProperty(value = "isPositive")
    public Boolean isPositive;
    private Integer useful;
    private Integer userId;
    private Integer filmId;


}
