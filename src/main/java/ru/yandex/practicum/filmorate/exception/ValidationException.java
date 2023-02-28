package ru.yandex.practicum.filmorate.exception;

import org.springframework.http.HttpStatus;

public class ValidationException extends RuntimeException{

    public ValidationException(HttpStatus code, String str) {
        super(str);
    }
}
