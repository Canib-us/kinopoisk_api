package com.kinopoisk.kinopoisk_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FiltersDTO {
    private List<CountryDTO> countries;
    private List<GenreDTO> genres;
}


