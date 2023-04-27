package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NotFoundException extends RuntimeException {

  public NotFoundException(String str) {
    super(str);
    log.warn(str);
  }
}
