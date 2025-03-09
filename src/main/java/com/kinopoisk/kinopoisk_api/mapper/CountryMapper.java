package com.kinopoisk.kinopoisk_api.mapper;

import com.kinopoisk.kinopoisk_api.dto.CountryDTO;
import com.kinopoisk.kinopoisk_api.entity.Country;
import org.springframework.stereotype.Component;

@Component
public class CountryMapper {
    public CountryDTO toDto(Country country) {
        if (country == null) {
            return null;
        }

        CountryDTO countryDTO = new CountryDTO();
        countryDTO.setId(country.getId());
        countryDTO.setCountry(country.getName());

        return countryDTO;
    }

    public Country toEntity(CountryDTO countryDTO) {
        if (countryDTO == null) {
            return null;
        }

        Country country = new Country();
        country.setId(countryDTO.getId());
        country.setName(countryDTO.getCountry());

        return country;
    }
}
