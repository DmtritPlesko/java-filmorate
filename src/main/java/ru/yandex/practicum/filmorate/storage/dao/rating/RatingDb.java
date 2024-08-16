package ru.yandex.practicum.filmorate.storage.dao.rating;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface RatingDb {

    Mpa getRatingById(Long id);

    List<Mpa> getAllRating();

}
