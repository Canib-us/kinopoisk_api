package com.kinopoisk.kinopoisk_api.controller;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.dto.FiltersDTO;
import com.kinopoisk.kinopoisk_api.entity.Film;
import com.kinopoisk.kinopoisk_api.service.EmailService;
import com.kinopoisk.kinopoisk_api.service.FilmService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/films")
public class FilmController {

    private FilmService filmService;
    private EmailService emailService;

    @GetMapping("/{id}")
    public Film getDataById(@PathVariable Long id){
        return filmService.getFilmFromApi(id);
    }

    @GetMapping("/type/{type}")
    public ResponseEntity <List<FilmDTO>> getFilmsByType(@PathVariable String type){
        try{
            List<FilmDTO> films = filmService.getFilmsByType(type);
            return ResponseEntity.ok(films);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/filters")
    public ResponseEntity<FiltersDTO> getFilmFilters(){
        try {
            FiltersDTO filters = filmService.getFilmFilters();
            return ResponseEntity.ok(filters);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<FilmDTO>> getAllFilms(){
        try {
            FiltersDTO filters = filmService.getFilmFilters();
            List<FilmDTO> films = filmService.getAllFilmsFromApi(filters.getCountries(), filters.getGenres());
            return ResponseEntity.ok(films);
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/getFromDB")
    public ResponseEntity<Page<FilmDTO>> getFilmsFromDB(
            @RequestParam Optional<String> name,
            @RequestParam Optional<Integer> yearFrom,
            @RequestParam Optional<Integer> yearTo,
            @RequestParam Optional<Double> ratingFrom,
            @RequestParam Optional<Double> ratingTo,
            @RequestParam Optional<String> email,
            @PageableDefault (size = 10) Pageable pageable){

        Page<FilmDTO> filmDTOS = filmService.getFilmsFromDB(
                name.orElse(null),
                yearFrom.orElse(null),
                yearTo.orElse(null),
                ratingFrom.orElse(null),
                ratingTo.orElse(null),
                pageable);

        email.ifPresent(emailAdress -> {
            String content = emailService.convertToXML(filmDTOS.getContent());
            emailService.sendSimpleMail(emailAdress, "Film`s for request", content);
        });
        return ResponseEntity.ok(filmDTOS);
    }




}


