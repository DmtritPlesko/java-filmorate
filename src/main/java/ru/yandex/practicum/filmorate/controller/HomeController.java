package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/home")
public class HomeController {
    @GetMapping


    private String hello() {
        return "<h1>Приветствуем вас, в приложении <b>по подбору фильмов</b><h1>";
    }
}
