package com.kinopoisk.kinopoisk_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmResponse {
    @JsonProperty("total")
    private int total;
    @JsonProperty("totalPages")
    private int totalPages;
    @JsonProperty("items")
    private FilmDTO[] items;
}


