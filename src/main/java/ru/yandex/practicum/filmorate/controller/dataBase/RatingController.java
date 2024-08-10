package ru.yandex.practicum.filmorate.controller.dataBase;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.RatingService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class RatingController {
    private final RatingService ratingService;

    @GetMapping("/{id}")
    public Mpa getRatingByID(@PathVariable("id") Long id) {
        return ratingService.getRatingById(id);
    }

    @GetMapping
    public List<Mpa> getAllRating() {
        return ratingService.getAllRating();
    }
}
