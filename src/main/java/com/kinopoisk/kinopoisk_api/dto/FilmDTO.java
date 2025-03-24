package com.kinopoisk.kinopoisk_api.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.kinopoisk.kinopoisk_api.entity.Genre;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class FilmDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long kinopoiskId;
    private String nameRu;
    private Integer year;
    private Double ratingKinopoisk;
    private String shortDescription;

    @JsonProperty("genres")
    private List<Genre> genres;

    @Data
    @NoArgsConstructor
    public static class Genre implements Serializable{
        private static final long serialVersionUID = 1L;

        private String genre;
    }
}


