package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dao.MpaDbStorage;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MPA;
import java.util.Collection;

@Slf4j
@RequiredArgsConstructor
@Service
public class MpaService {

    private final MpaDbStorage mpaDbStorage;

    public Collection<MPA> getAllMpa() {
        return mpaDbStorage.getAllMpa();
    }

    public MPA getMpaById(int id) {
        MPA mpa = mpaDbStorage.getMpaById(id);
        if (mpa == null) {
            throw new NotFoundException("MPA с id=" + id + "не найден");
        }
        return mpa;
    }
}