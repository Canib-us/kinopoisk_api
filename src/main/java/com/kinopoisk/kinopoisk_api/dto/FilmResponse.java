package com.kinopoisk.kinopoisk_api.dto;

import lombok.Data;

@Data
public class FilmResponse {
    private int total;
    private int totalPages;
    private FilmDTO[] items;
}
