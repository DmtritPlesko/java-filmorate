package ru.yandex.practicum.filmorate.controller.dataBase;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.service.DirectorDbService;

@RestController
@RequestMapping("/directors")
public class DirectorDbController {
    private final DirectorDbService directorDbService;

    @Autowired
    public DirectorDbController(DirectorDbService directorDb) {
        this.directorDbService = directorDb;
    }

    //create
    @PostMapping
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
    public void deleteDirectorById(@PathVariable("id") Long id) {
        directorDbService.deleteDirectorById(id);
    }

}
