package com.kinopoisk.kinopoisk_api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmDTO {
    private Long kinopoiskId;
    private String nameRu;
    private Integer year;
    private Double ratingKinopoisk;
    private String shortDescription;
}


