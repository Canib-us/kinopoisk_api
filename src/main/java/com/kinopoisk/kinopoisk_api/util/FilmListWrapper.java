package com.kinopoisk.kinopoisk_api.util;

import com.kinopoisk.kinopoisk_api.dto.FilmDTO;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@XmlRootElement(name = "films")
@XmlAccessorType(XmlAccessType.FIELD)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilmListWrapper {
    @XmlElement(name = "film")
    private List<FilmDTO> films;
}
