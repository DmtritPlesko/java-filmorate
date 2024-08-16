package ru.yandex.practicum.filmorate.controller.dataBase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Status;
import ru.yandex.practicum.filmorate.service.StatusService;

import java.util.List;

@RestController
@RequestMapping("/status")
public class StatusController {
    private final StatusService statusService;

    @Autowired
    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @GetMapping("/{id}")
    public Status getStatusById(@PathVariable("id") Long statusId) {
        return statusService.getStatusById(statusId);
    }

    @GetMapping
    public List<Status> getAllStatus() {
        return statusService.getAllStatus();
    }
}
