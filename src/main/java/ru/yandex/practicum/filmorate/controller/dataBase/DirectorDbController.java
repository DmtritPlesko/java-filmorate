package ru.yandex.practicum.filmorate.controller.dataBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorDbService;

import java.util.List;

@RestController
@RequestMapping("/directors")
public class DirectorDbController {
    private final DirectorDbService directorDbService;

    @Autowired
    public DirectorDbController(DirectorDbService directorDbService) {
        this.directorDbService = directorDbService;
    }

    //create
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director createDirector(@RequestBody Director director) {
        return directorDbService.createDirector(director);
    }

    //read
    @GetMapping
    public List<Director> getAllDirectors() {
        return directorDbService.getAllDirectors();
    }

    @GetMapping("/{id}")
    public Director getDirectorById(@PathVariable("id") Long id) {
        return directorDbService.getDirectorById(id);
    }

    //update
    @PutMapping
    public Director updateDirector(@RequestBody Director director) {
        return directorDbService.updateDirector(director);
    }

    //delete
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDirectorById(@PathVariable("id") Long id) {
        directorDbService.deleteDirectorById(id);
    }
}
