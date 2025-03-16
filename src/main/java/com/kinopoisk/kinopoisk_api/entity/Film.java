package com.kinopoisk.kinopoisk_api.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Film {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long filmId;

    private String filmName;

    private Integer year;

    private Double rating;

    private String description;

}


