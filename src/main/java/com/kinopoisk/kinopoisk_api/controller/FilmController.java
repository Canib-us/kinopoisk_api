package com.kinopoisk.kinopoisk_api.controller;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.dto.FiltersDTO;
import com.kinopoisk.kinopoisk_api.service.EmailService;
import com.kinopoisk.kinopoisk_api.service.FilmService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v2/films")
public class FilmController {

    private FilmService filmService;
    private EmailService emailService;

    @GetMapping("/filters")
    public ResponseEntity<FiltersDTO> getFilters(){
        try{
            filmService.fetchAndSaveCountriesAndGenres();
            return ResponseEntity.ok(new FiltersDTO());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @GetMapping("/fetchAll")
    public ResponseEntity<String> fetchAndSaveFilms(){
        try {
            filmService.fetchAndSaveAllFilms();
            return ResponseEntity.ok("All films saved");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while fetching all films");
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
            @PageableDefault (size = 20) Pageable pageable){

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


