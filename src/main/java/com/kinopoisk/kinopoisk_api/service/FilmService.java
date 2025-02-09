package com.kinopoisk.kinopoisk_api.service;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import com.kinopoisk.kinopoisk_api.dto.FilmResponse;
import com.kinopoisk.kinopoisk_api.entity.Film;
import com.kinopoisk.kinopoisk_api.repository.FilmRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class FilmService {

    @Autowired
    private RestTemplate restTemplate;

    private final String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films";
    private final String token = "003c833e-960e-448c-a10d-76863ff7e79d";
    @Autowired
    private FilmRepository filmRepository;

    public Film getFilmFromApi(Long filmId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String urlId=url+"/{id}";

        ResponseEntity<FilmDTO> response = restTemplate.exchange(urlId, HttpMethod.GET, entity, FilmDTO.class, filmId);

        if (!response.getStatusCode().is2xxSuccessful()||response.getBody()==null) {
            throw new RuntimeException("Couldn't get movie data with id: "+filmId);
        }

        FilmDTO filmDTO = response.getBody();
        Double rating = filmDTO.getRatingKinopoisk()!=null?filmDTO.getRatingKinopoisk():0.0;
        if(filmRepository.existsByFilmId(filmDTO.getKinopoiskId())){
            return filmRepository.findByFilmId(filmDTO.getKinopoiskId()).orElseThrow();
        }
        return filmRepository.save(Film.builder()
                .filmId(filmDTO.getKinopoiskId())
                .filmName(filmDTO.getNameRu())
                .year(filmDTO.getYear())
                .rating(filmDTO.getRatingKinopoisk())
                .description(filmDTO.getShortDescription())
                .build());
    }

//    public void searchFilmsByType(String type) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("X-API-KEY", token);
//        HttpEntity<String> entity = new HttpEntity<>(headers);
//
//        String urlType=url+"?type="+type;
//        ResponseEntity<FilmDTO[]> response = restTemplate.exchange(urlType, HttpMethod.GET, entity, FilmDTO[].class, type);
//
//        if (!response.getStatusCode().is2xxSuccessful()||response.getBody()==null) {
//            throw new RuntimeException("Couldn't get movie data with type: "+type);
//        }
//
//        FilmDTO[] filmDTOS = response.getBody();
//        for (FilmDTO filmDTO : filmDTOS) {
//            if(!filmRepository.existsByFilmId(filmDTO.getKinopoiskId())){
//                Film newFilm = Film.builder()
//                        .filmId(filmDTO.getKinopoiskId())
//                        .filmName(filmDTO.getNameRu())
//                        .year(filmDTO.getYear())
//                        .rating(filmDTO.getRatingKinopoisk()!=null?filmDTO.getRatingKinopoisk():0.0)
//                        .description(filmDTO.getShortDescription())
//                        .build();
//                filmRepository.save(newFilm);
//            }
//        }
//    }

    public FilmDTO[] getFilmsByType(String type) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", token); // Установка заголовка с токеном
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlType = url + "?type=" + type; // Формирование URL с типом фильма
        ResponseEntity<FilmResponse> response = restTemplate.exchange(urlType, HttpMethod.GET, entity, FilmResponse.class);

        // Проверка успешности ответа
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Couldn't get movie data with type: " + type);
        }

        return response.getBody().getItems(); // Возвращаем массив фильмов
    }

}


