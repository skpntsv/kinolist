package ru.nsu.kinolist.filmApi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class FilmResponse {
    private int filmId;

    private String nameRu;

    private String type;

    private int year;

    private String description;

    private double rating;

    private String posterUrl;

    private List<Genre> genres;

}
