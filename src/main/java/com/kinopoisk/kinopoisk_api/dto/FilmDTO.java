package com.kinopoisk.kinopoisk_api.dto;

import lombok.Data;

@Data
public class FilmDTO {
    private Long kinopoiskId;
    private String nameRu;
    private Integer year;
    private Double ratingKinopoisk;
    private String shortDescription;
}


