package ru.nsu.kinolist.filmApi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class FilmResponseByRandom {
    private int kinopoiskId;

    private String nameRu;

    private String nameEn;

    private List<Genre> genres;

    private Double ratingKinopoisk;

    private Double ratingImdb;

    private String type;

    private int year;

    private String posterUrl;
}
