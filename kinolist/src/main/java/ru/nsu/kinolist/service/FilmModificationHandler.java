package ru.nsu.kinolist.service;

import org.springframework.stereotype.Component;
import ru.nsu.kinolist.filmApi.response.FilmResponse;
import ru.nsu.kinolist.filmApi.service.FilmApiService;
import ru.nsu.kinolist.jobs.Film;
import ru.nsu.kinolist.jobs.FilmDAO;

import java.util.Optional;

@Component
public class FilmModificationHandler {
    private FilmApiService filmApiService;
    private FilmDAO filmDAO;

    public Optional<Film> findFilm(String filmName) {
        Optional<Film> filmFromDB = filmDAO.getFilm(filmName);
        if (filmFromDB.isPresent()) {
            return filmFromDB;
        }
        Optional<FilmResponse> optFilmResponse = filmApiService.sendRequestByName(filmName);
        if (optFilmResponse.isEmpty()) {
            return Optional.empty();
        }

        Film film = toFilm(optFilmResponse.get());
        filmDAO.saveFilm(film);
        return Optional.of(film);
    }

    private Film toFilm(FilmResponse filmResponse) {
        return new Film(filmResponse.getNameRu(), filmResponse.getYear(), filmResponse.getPosterUrl(), filmResponse.getFilmId(),
                !filmResponse.getType().equals("FILM"), filmResponse.getRating(), filmResponse.getDescription());
    }

}
