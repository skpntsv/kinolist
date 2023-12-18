package ru.nsu.kinolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.database.entities.Film;
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

    public Film getRandomFilm(Categories categories) {
        return toFilm(filmApiService.sendRequestForRandomFilm(categories));
    }

    private Film toFilm(FilmResponseByRandom filmResponseByRandom) {
        String descr = filmApiService.sendRequestForDescrById(filmResponseByRandom.getKinopoiskId());
        return new Film(filmResponseByRandom.getNameRu(), filmResponseByRandom.getYear(),
                filmResponseByRandom.getGenres().get(0).getGenre(), filmResponseByRandom.getPosterUrl(),
                filmResponseByRandom.getKinopoiskId(), !filmResponseByRandom.getType().equals("FILM"),
                filmResponseByRandom.getRatingKinopoisk(), descr);
    }
}
