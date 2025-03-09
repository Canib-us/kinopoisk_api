package com.kinopoisk.kinopoisk_api.mapper;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.entity.Film;
import org.springframework.stereotype.Component;

@Component
public class FilmMapper{
    public FilmDTO toDto(Film film) {
        if (film == null) {
            return null;
        }

        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setKinopoiskId(film.getFilmId());
        filmDTO.setNameRu(film.getFilmName());
        filmDTO.setYear(film.getYear());
        filmDTO.setRatingKinopoisk(film.getRating());
        filmDTO.setShortDescription(film.getDescription());

        return filmDTO;
    }

    public Film toEntity(FilmDTO filmDTO) {
        if (filmDTO == null) {
            return null;
        }

        Film film = new Film();
        film.setFilmId(filmDTO.getKinopoiskId());
        film.setFilmName(filmDTO.getNameRu());
        film.setYear(filmDTO.getYear());
        film.setRating(filmDTO.getRatingKinopoisk()!=null?filmDTO.getRatingKinopoisk():0);
        film.setDescription(filmDTO.getShortDescription());

        return film;
    }
}
