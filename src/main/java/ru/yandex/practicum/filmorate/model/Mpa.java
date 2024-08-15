package ru.yandex.practicum.filmorate.model;

import lombok.Data;

@Data
public class Mpa {

    private Long id;

    private String name;

    public Mpa(Long id) {
        this.id = id;
    }

    public Mpa(Long id, String ratingName) {
        this.id = id;
        this.name = ratingName;
    }
}
