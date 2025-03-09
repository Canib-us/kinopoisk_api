package com.kinopoisk.kinopoisk_api.repository;

import com.kinopoisk.kinopoisk_api.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
}
