package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Director {

  @EqualsAndHashCode.Exclude
  Integer id;
  @NotBlank
  String name;
}
