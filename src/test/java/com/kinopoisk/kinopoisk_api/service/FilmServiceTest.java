package com.kinopoisk.kinopoisk_api.service;

import com.kinopoisk.kinopoisk_api.dto.*;
import com.kinopoisk.kinopoisk_api.entity.Country;
import com.kinopoisk.kinopoisk_api.entity.Film;
import com.kinopoisk.kinopoisk_api.entity.Genre;
import com.kinopoisk.kinopoisk_api.mapper.CountryMapper;
import com.kinopoisk.kinopoisk_api.mapper.FilmMapper;
import com.kinopoisk.kinopoisk_api.mapper.GenreMapper;
import com.kinopoisk.kinopoisk_api.repository.CountryRepository;
import com.kinopoisk.kinopoisk_api.repository.FilmRepository;
import com.kinopoisk.kinopoisk_api.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FilmServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private FilmRepository filmRepository;
    @Mock
    private CountryRepository countryRepository;
    @Mock
    private GenreRepository genreRepository;
    @Mock
    private FilmMapper filmMapper;
    @Mock
    private CountryMapper countryMapper;
    @Mock
    private GenreMapper genreMapper;
    @InjectMocks
    private FilmService filmService;

    private final String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films/filters";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void fetchAndSaveCountriesAndGenresTest(){
        //1 stage
        CountryDTO countryDTO = new CountryDTO(1, "Russia");
        GenreDTO genreDTO = new GenreDTO(1, "Comedy");
        FiltersDTO filtersDTO = new FiltersDTO(List.of(countryDTO), List.of(genreDTO));

        ResponseEntity<FiltersDTO> mockResponse = new ResponseEntity<>(filtersDTO, HttpStatus.OK);

        when(restTemplate.exchange(eq(url), eq(HttpMethod.GET), any(HttpEntity.class), eq(FiltersDTO.class)))
                .thenReturn(mockResponse);

        Country countryEntity = new Country();
        countryEntity.setId(1);
        countryEntity.setName("Russia");

        Genre genreEntity = new Genre();
        genreEntity.setId(1);
        genreEntity.setName("Comedy");

        when(countryMapper.toEntity(countryDTO)).thenReturn(countryEntity);
        when(genreMapper.toEntity(genreDTO)).thenReturn(genreEntity);

        //2 stage
        filmService.fetchAndSaveCountriesAndGenres();

        //3 stage
        verify(countryRepository, times(1)).save(countryEntity);
        verify(genreRepository, times(1)).save(genreEntity);
    }

    @Test
    void saveFilmToDBTest(){
        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setKinopoiskId(1L);
        filmDTO.setNameRu("First film");

        FilmDTO filmDTO1 = new FilmDTO();
        filmDTO1.setKinopoiskId(2L);
        filmDTO1.setNameRu("Second film");

        Film film = new Film();
        film.setFilmId(1L);
        film.setFilmName("First film");

        Film film1 = new Film();
        film1.setFilmId(2L);
        film1.setFilmName("Second film");


        FilmDTO[] filmDTOs = {filmDTO, filmDTO1};

        when(filmRepository.existsByFilmId(1L)).thenReturn(true);
        when(filmRepository.existsByFilmId(2L)).thenReturn(false);
        when(filmMapper.toEntity(filmDTO1)).thenReturn(film1);

        filmService.saveFilmToDB(filmDTOs);

        verify(filmRepository, times(1)).existsByFilmId(1L);
        verify(filmRepository, times(1)).existsByFilmId(2L);

        verify(filmMapper, times(0)).toEntity(filmDTO);
        verify(filmMapper, times(1)).toEntity(filmDTO1);

        verify(filmRepository, times(0)).save(film);
        verify(filmRepository, times(1)).save(film1);
    }

    @Test
    void fetchAndSaveAllFilmsTest(){
        List<CountryDTO> countriesDTOList = List.of(new CountryDTO(1, "Russia"));
        List<GenreDTO> genreDTOList = List.of(new GenreDTO(1, "Comedy"));
        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setKinopoiskId(1L);
        FilmResponse filmResponse = new FilmResponse();
        filmResponse.setItems(new FilmDTO[]{filmDTO});
        filmResponse.setTotalPages(1);

        FilmService spyFilmService = Mockito.spy(filmService);
        doReturn(countriesDTOList).when(spyFilmService).getAllCountries();
        doReturn(genreDTOList).when(spyFilmService).getAllGenres();

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(FilmResponse.class)))
                .thenReturn(new ResponseEntity<>(filmResponse, HttpStatus.OK));

        spyFilmService.fetchAndSaveAllFilms();

        verify(restTemplate, times(1)).exchange(anyString(), eq(HttpMethod.GET), any(HttpEntity.class), eq(FilmResponse.class));
        verify(spyFilmService, times(1)).saveFilmToDB(any(FilmDTO[].class));
    }

    @Test
    void getFilmSFromDBTest(){
        Pageable pageable = PageRequest.of(0, 10);
        Film film = new Film();
        film.setFilmName("Bubochka");
        film.setYear(2010);
        film.setRating(9.9);

        FilmDTO filmDTO = new FilmDTO();
        filmDTO.setNameRu("Bubochka");
        filmDTO.setYear(2010);
        filmDTO.setRatingKinopoisk(9.9);

        List<Film> filmList = List.of(film);
        Page<Film> filmPage = new PageImpl<>(filmList);

        when(filmRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(filmPage);
        when(filmMapper.toDto(film)).thenReturn(filmDTO);

        Page<FilmDTO> result = filmService.getFilmsFromDB("Bubochka", 2000, 2020, 8.9,
                9.99, pageable);

        verify(filmRepository, times(1)).findAll(any(Specification.class), eq(pageable));

        assertFalse(result.isEmpty());

        assertEquals("Bubochka", result.getContent().get(0).getNameRu());
        assertEquals(2010, result.getContent().get(0).getYear());
        assertEquals(9.9, result.getContent().get(0).getRatingKinopoisk());

    }
}
