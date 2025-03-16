package com.kinopoisk.kinopoisk_api.controller;

import com.kinopoisk.kinopoisk_api.entity.Film;
import com.kinopoisk.kinopoisk_api.repository.FilmRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class FilmControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private FilmRepository filmRepository;

        @Test
    void getFilmsFromDB_ReturnsFilteredFilms() throws Exception {
        Film film = Film.builder()
                .filmId(1L)
                .filmName("The Matrix Test")
                .year(2013)
                .rating(8.9)
                .description("I`m your father, Neo")
                .build();
        filmRepository.save(film);
        Film film1 = Film.builder()
                .filmId(2L)
                .filmName("Wuabawuau Test")
                .year(2014)
                .rating(9.9)
                .description("The best film in your life")
                .build();
        filmRepository.save(film1);

        mockMvc.perform(get("/api/v2/films/getFromDB")
                        .param("name", "Test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].nameRu").value("The Matrix Test"))
                .andExpect(jsonPath("$.content[1].nameRu").value("Wuabawuau Test"));
    }
}
