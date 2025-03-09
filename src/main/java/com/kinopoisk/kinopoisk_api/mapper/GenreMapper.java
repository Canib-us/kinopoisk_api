package com.kinopoisk.kinopoisk_api.mapper;

import com.kinopoisk.kinopoisk_api.dto.GenreDTO;
import com.kinopoisk.kinopoisk_api.entity.Genre;
import org.springframework.stereotype.Component;

@Component
public class GenreMapper {
    public GenreDTO toDto(Genre genre) {
        if (genre == null) {
            return null;
        }

        GenreDTO genreDTO = new GenreDTO();
        genreDTO.setId(genre.getId());
        genreDTO.setGenre(genre.getName());

        return genreDTO;
    }

    public Genre toEntity(GenreDTO genreDTO) {
        if (genreDTO == null) {
            return null;
        }

        Genre genre = new Genre();
        genre.setId(genreDTO.getId());
        genre.setName(genreDTO.getGenre());

        return genre;
    }
}
