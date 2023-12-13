package ru.nsu.kinolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.filmApi.response.Categories;
import ru.nsu.kinolist.filmApi.response.FilmResponseByRandom;
import ru.nsu.kinolist.filmApi.service.FilmApiService;

@Component
public class RandomFilmController {
    private final FilmApiService filmApiService;

    @Autowired
    public RandomFilmController(FilmApiService filmApiService) {
        this.filmApiService = filmApiService;
    }

    public FilmResponseByRandom getRandomFilm(Categories categories) {
        return filmApiService.sendRequestForRandomFilm(categories);
    }
}
