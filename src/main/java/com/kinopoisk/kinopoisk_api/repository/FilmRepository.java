package com.kinopoisk.kinopoisk_api.repository;

import com.kinopoisk.kinopoisk_api.entity.Film;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FilmRepository extends JpaRepository<Film, Long>, JpaSpecificationExecutor<Film> {
    boolean existsByFilmId(Long filmId);
    Optional<Film> findByFilmId(Long filmId);
}


