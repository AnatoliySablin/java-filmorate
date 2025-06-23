package ru.yandex.practicum.filmorate.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.List;

@RestController
@RequestMapping({"/mpa"})
@Slf4j
@Validated
@RequiredArgsConstructor
public class MpaController {
    private final MpaService mpaService;

    @GetMapping()
    public List<Mpa> listMpa() {
        log.info("Получаем список рейтингов, их количество: " + mpaService.listMpa().size());
        return mpaService.listMpa();
    }

    @GetMapping("/{id}")
    public Mpa getMpaById(@PathVariable Long id) {
        log.info("Получаем рейтинг по id: " + id);
        return mpaService.getMpaById(id);
    }
}