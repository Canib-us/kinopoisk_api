package com.kinopoisk.kinopoisk_api.service;

import com.kinopoisk.kinopoisk_api.dto.*;
import com.kinopoisk.kinopoisk_api.entity.Film;
import com.kinopoisk.kinopoisk_api.repository.FilmRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
@AllArgsConstructor
public class FilmService {

    @Autowired
    private RestTemplate restTemplate;

    private final String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films";
    private final String token = "003c833e-960e-448c-a10d-76863ff7e79d";
    @Autowired
    private FilmRepository filmRepository;

    //to api start
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


    public List<FilmDTO> getFilmsByType(String type) {
        List<FilmDTO> allFilms = new ArrayList<>();
        int page = 1;
        boolean hasMorePages = true;

        while (hasMorePages) {
            HttpHeaders headers = new HttpHeaders();
            headers.set("X-API-KEY", token);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String urlType = url+"?type="+type+"&page="+page;
            ResponseEntity<FilmResponse> response = restTemplate.exchange(urlType, HttpMethod.GET, entity, FilmResponse.class);

            if (!response.getStatusCode().is2xxSuccessful()||response.getBody()==null) {
                throw new RuntimeException("Couldn't get movie data with type: "+type);
            }

            FilmDTO[] films = response.getBody().getItems();
            allFilms.addAll(Arrays.asList(films));
            saveFilmToDB(films);

            hasMorePages = page<response.getBody().getTotalPages();
            page++;

        }
        return allFilms;
    }

    public FiltersDTO getFilmFilters(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String urlFilters = url+"/filters";
        ResponseEntity<FiltersDTO> response = restTemplate.exchange(urlFilters, HttpMethod.GET, entity, FiltersDTO.class);

        if (!response.getStatusCode().is2xxSuccessful()||response.getBody()==null) {
            throw new RuntimeException("Couldn't get filters data");
        }

        return response.getBody();
    }

    public List<FilmDTO> getAllFilmsFromApi(List<CountryDTO> countries, List<GenreDTO> genres) {
        List<FilmDTO> allFilms = new ArrayList<>();

        Semaphore semaphore = new Semaphore(5);
        int totalReq = 0;

        for (CountryDTO country : countries) {
            for (GenreDTO genre : genres) {
                int page = 1;
                boolean hasMorePages = true;

                while (hasMorePages&&totalReq<=500) {
                    String requestUrl = String.format("%s?countryId=%d&genreId=%d&page=%d", url, country.getId(), genre.getId(), page);
                    HttpHeaders headers = new HttpHeaders();
                    headers.set("X-API-KEY", token);
                    HttpEntity<String> entity = new HttpEntity<>(headers);

                    try {
                        semaphore.acquire();
                        Logger.getLogger(FilmService.class.getName()).log(Level.INFO, "Request URL: " + requestUrl);
                        ResponseEntity<FilmResponse> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, FilmResponse.class);
                        Logger.getLogger(FilmService.class.getName()).log(Level.INFO, "Response: " + response.getBody());

                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            FilmDTO[] films = response.getBody().getItems();
                            allFilms.addAll(Arrays.asList(films));
                            saveFilmToDB(films);
                            hasMorePages = page < response.getBody().getTotalPages();
                            page++;
                        } else {
                            hasMorePages = false;
                        }

                        totalReq++;
                        if(totalReq%5==0){
                            Thread.sleep(1000);
                        }

                    } catch (HttpClientErrorException e) {
                        Logger.getLogger(FilmService.class.getName()).log(Level.SEVERE, "Client error: " + e.getMessage(), e);
                        hasMorePages = false;
                    } catch (Exception e) {
                        Logger.getLogger(FilmService.class.getName()).log(Level.SEVERE, "Error searching films", e);
                        hasMorePages = false;
                    } finally {
                        semaphore.release();
                    }
                }
            }
        }

        return allFilms;
    }

    public void saveFilmToDB(FilmDTO[] filmDTOS){
        for (FilmDTO filmDTO : filmDTOS) {
            if(!filmRepository.existsByFilmId(filmDTO.getKinopoiskId())){
                Film film = Film.builder()
                        .filmId(filmDTO.getKinopoiskId())
                        .filmName(filmDTO.getNameRu())
                        .year(filmDTO.getYear())
                        .rating(filmDTO.getRatingKinopoisk())
                        .description(filmDTO.getShortDescription())
                        .build();
                filmRepository.save(film);
            }
        }
    }
    //to api end

    //to db start
    public Page<FilmDTO> getFilmsFromDB(String name, Integer yearFrom, Integer yearTo, Double ratingFrom,
                                        Double ratingTo, Pageable pageable){
        //specification`s start
        Specification<Film> spec = new Specification<Film>() {
            @Override
            public Predicate toPredicate(Root<Film> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
                Predicate predicate = criteriaBuilder.conjunction();

                if(name!=null){
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.like(root.get("filmName"),"%"+name+"%"));
                }
                if(yearFrom!=null){
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.greaterThanOrEqualTo(root.get("year"),yearFrom));
                }
                if (yearTo!=null){
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.lessThanOrEqualTo(root.get("year"),yearTo));
                }
                if(ratingFrom!=null){
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.greaterThanOrEqualTo(root.get("rating"),ratingFrom));
                }
                if(ratingTo!=null){
                    predicate = criteriaBuilder.and(predicate,
                            criteriaBuilder.lessThanOrEqualTo(root.get("rating"),ratingTo));
                }
                return predicate;
            }
        };
        //specification`s end
        Page<Film> filmsPage = filmRepository.findAll(spec, pageable);

        return filmsPage.map(film -> {
            FilmDTO filmDTO = new FilmDTO();
            filmDTO.setKinopoiskId(film.getFilmId());
            filmDTO.setNameRu(film.getFilmName());
            filmDTO.setYear(film.getYear());
            filmDTO.setRatingKinopoisk(film.getRating());
            filmDTO.setShortDescription(film.getDescription());
            return filmDTO;
        });

    }
}




