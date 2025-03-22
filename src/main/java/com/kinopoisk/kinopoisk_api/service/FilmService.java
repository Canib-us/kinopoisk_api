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
import jakarta.persistence.EntityManager;
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

import java.util.List;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor
public class FilmService {

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private EntityManager entityManager;

    private final String url = "https://kinopoiskapiunofficial.tech/api/v2.2/films";
    private final String token = "fe6535d7-ffa0-4df9-b990-6262c165a310";
    @Autowired
    private FilmRepository filmRepository;
    @Autowired
    private CountryRepository countryRepository;
    @Autowired
    private GenreRepository genreRepository;
    @Autowired
    private FilmMapper filmMapper;
    @Autowired
    private CountryMapper countryMapper;
    @Autowired
    private GenreMapper genreMapper;

//to api start
    public void fetchAndSaveCountriesAndGenres(){
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-API-KEY", token);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String urlFilters = url+"/filters";

        ResponseEntity<FiltersDTO> response = restTemplate.exchange(urlFilters, HttpMethod.GET, entity, FiltersDTO.class);

        if (response.getStatusCode().is2xxSuccessful()&&response.getBody()!=null) {
            FiltersDTO filtersDTO = response.getBody();
            if(filtersDTO.getCountries()!=null){
                for (CountryDTO countryDTO : filtersDTO.getCountries()) {
                    Country newCountry = countryMapper.toEntity(countryDTO);
                    countryRepository.save(newCountry);
                }
            }
            if (filtersDTO.getGenres() != null) {
                for (GenreDTO genreDTO : filtersDTO.getGenres()) {
                    Genre newGenre = genreMapper.toEntity(genreDTO);
                    genreRepository.save(newGenre);
                }
            }
        }
    }

    public List<CountryDTO> getAllCountries() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Country> query = cb.createQuery(Country.class);
        Root<Country> root = query.from(Country.class);
        query.select(root);
        List<Country> countries = entityManager.createQuery(query).getResultList();

        return countries.stream()
                .map(countryMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<GenreDTO> getAllGenres() {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Genre> query = cb.createQuery(Genre.class);
        Root<Genre> root = query.from(Genre.class);
        query.select(root);
        List<Genre> genres = entityManager.createQuery(query).getResultList();

        return genres.stream()
                .map(genreMapper::toDto)
                .collect(Collectors.toList());
    }

    public void saveFilmToDB(FilmDTO[] filmDTOS){
        for (FilmDTO filmDTO : filmDTOS) {
            if(!filmRepository.existsByFilmId(filmDTO.getKinopoiskId())){
                Film film = filmMapper.toEntity(filmDTO);
                filmRepository.save(film);
            }
        }
    }
    public void fetchAndSaveAllFilms(){
        List<CountryDTO> countries = getAllCountries();
        List<GenreDTO> genres = getAllGenres();

        int totalReq = 0;
        int maxReq = 500;

        for (CountryDTO country : countries) {
            for (GenreDTO genre : genres) {
                int page = 1;
                boolean hasMorePages = true;

                while (hasMorePages && totalReq <= maxReq) {
                    String reqURL = String.format("%s?countries=%d&genres=%d&page=%d", url,
                            country.getId(), genre.getId(), page);

                    HttpHeaders headers = new HttpHeaders();
                    headers.set("X-API-KEY", token);
                    HttpEntity<String> entity = new HttpEntity<>(headers);

                    try{
                        ResponseEntity<FilmResponse> response = restTemplate.exchange(reqURL,
                                HttpMethod.GET, entity, FilmResponse.class);
                        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                            FilmDTO[] films = response.getBody().getItems();
                            saveFilmToDB(films);
                            hasMorePages = page < response.getBody().getTotalPages();
                            page++;
                        }else {
                            hasMorePages = false;
                        }
                        totalReq++;
                    } catch (HttpClientErrorException e) {
                        hasMorePages = false;
                    } catch (Exception e) {
                        hasMorePages = false;
                    }
                }
            }
        }
    }
    //to api end

    //to db start
    public Page<FilmDTO> getFilmsFromDB(String name, Integer yearFrom, Integer yearTo, Double ratingFrom,
                                        Double ratingTo, Pageable pageable){
        Specification<Film> spec = (root, query, criteriaBuilder) -> {
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
        };

        System.out.println("Executing query with spec: " + spec);
        Page<Film> filmsPage = filmRepository.findAll(spec, pageable);
        System.out.println("Query result: " + filmsPage);
        if (filmsPage == null) {
            return Page.empty();
        }
        return filmsPage.map(filmMapper::toDto);
    }

}




