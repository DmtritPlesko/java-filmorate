package ru.yandex.practicum.filmorate.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.dao.rating.RatingDb;
import ru.yandex.practicum.filmorate.storage.dao.rating.RatingDbStorage;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class RatingService {
    private RatingDb ratingStorage;

    @Autowired
    public RatingService(RatingDbStorage ratingStorage) {
        this.ratingStorage = ratingStorage;
    }

    public Mpa getRatingById(Long id) {
        return ratingStorage.getRatingById(id);
    }

    public List<Mpa> getAllRating() {
        return ratingStorage.getAllRating();
    }

}
