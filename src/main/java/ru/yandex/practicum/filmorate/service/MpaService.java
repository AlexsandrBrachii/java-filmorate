package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.impl.MpaStorageImpl;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {

    private final MpaStorageImpl mpaDbStorage;

    public Collection<Mpa> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    public Mpa getMpaById(int id) {
        Mpa mpa = mpaDbStorage.getMpaById(id);
        if (mpa == null) {
            throw new NotFoundException("MPA с id=" + id + "не найден");
        }
        return mpa;
    }
}
