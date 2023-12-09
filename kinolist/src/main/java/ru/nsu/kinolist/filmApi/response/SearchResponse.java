package ru.nsu.kinolist.filmApi.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Getter
public class SearchResponse {
    private String keyword;

    private List<FilmResponse> films;

    private int searchFilmsCountResult;
}
