package ru.nsu.kinolist.filmApi.response;

import lombok.Getter;

import java.util.List;

@Getter
public class RandomFilmsResponse {
    int total;
    int totalPages;
    List<FilmResponseByRandom> items;
}
