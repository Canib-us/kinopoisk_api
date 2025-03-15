package com.kinopoisk.kinopoisk_api.controller;


import com.kinopoisk.kinopoisk_api.entity.Film;
import com.kinopoisk.kinopoisk_api.repository.FilmRepository;
import com.kinopoisk.kinopoisk_api.service.EmailService;
import com.kinopoisk.kinopoisk_api.service.FilmService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@Transactional
public class FilmControllerIntegrationTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    FilmService filmService;

    @Autowired
    EmailService emailService;

    @Autowired
    FilmRepository filmRepository;

    @BeforeEach
    void setUp() {
        doNothing().when(filmService).fetchAndSaveCountriesAndGenres();
        doNothing().when(filmService).fetchAndSaveAllFilms();
    }

    @Test
    @Sql(scripts = "test/resources/db/migration/V1__CREATE_FILMS_TABLE.sql")
    void getFiltersTest() throws Exception {
        mockMvc.perform(get("/api/v2/films/filters"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").exists());

        verify(filmService, times(1)).fetchAndSaveCountriesAndGenres();
    }

    @Test
    void fetchAndSaveFilmsTest() throws Exception {
        mockMvc.perform(get("/api/v2/films/fetchAll"))
                .andExpect(status().isOk())
                .andExpect(content().string("All films saved"));

        verify(filmService, times(1)).fetchAndSaveAllFilms();
    }

    @Test
    void getFilmsFromDBTest() throws Exception {
        Film film = new Film(1L, 1L, "NoHomo", 1983, 8.9, "By DJ Pivo");
        filmRepository.save(film);

        mockMvc.perform(get("/api/v2/films/getFromDB")
                .param("name", film.getFilmName())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray());

        verify(filmService, times(1)).getFilmsFromDB(any(), any(), any(), any(), any(), any());
        verify(emailService, times(1)).sendSimpleMail(anyString(), anyString(), anyString());

    }
}
