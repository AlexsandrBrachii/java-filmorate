package ru.yandex.practicum.filmorate.exception;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DirectorException extends RuntimeException {

  public DirectorException(String message) {
    super(message);
    log.warn(message);
  }
}
