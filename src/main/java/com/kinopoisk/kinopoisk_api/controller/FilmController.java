package com.kinopoisk.kinopoisk_api.controller;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.entity.Film;
import com.kinopoisk.kinopoisk_api.service.FilmService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/films")
public class FilmController {

    private FilmService filmService;

    @GetMapping("/{id}")
    public Film getDataById(@PathVariable Long id){
        return filmService.getFilmFromApi(id);
    }
//    @GetMapping("/saveByType")
//    public void searchFilmsGroupByType(@RequestParam String type){
//        filmService.searchFilmsByType(type);
//    }

    @GetMapping("/listByType") // Новый эндпоинт для получения списка фильмов по типу
    public FilmDTO[] getFilmsByType(@RequestParam String type) {
        return filmService.getFilmsByType(type); // Вызов сервиса для получения фильмов
    }


}


