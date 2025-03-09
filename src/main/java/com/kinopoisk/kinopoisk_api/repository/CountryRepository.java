package com.kinopoisk.kinopoisk_api.repository;

import com.kinopoisk.kinopoisk_api.entity.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {
}
