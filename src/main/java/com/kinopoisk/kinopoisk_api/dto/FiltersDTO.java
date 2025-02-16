package com.kinopoisk.kinopoisk_api.dto;

import lombok.Data;

import java.util.List;

@Data
public class FiltersDTO {
    private List<CountryDTO> countries;
    private List<GenreDTO> genres;
}


