package ru.nsu.kinolist.service;

import org.springframework.stereotype.Component;
import ru.nsu.kinolist.filmApi.response.FilmResponse;
import ru.nsu.kinolist.filmApi.service.FilmApiService;

import java.util.Optional;

@Component
public class FilmModificationHandler {
    private FilmApiService filmApiService;
    private FilmDAO filmDAO;

    public Optional<Film> findFilm(String filmName, ControllerType from) {
        //TODO switch by type
        Optional<Film> filmFromDB = filmDAO.getFilm(filmName);
        if (filmFromDB.isPresent()) {
            return filmFromDB;
        }
        Optional<FilmResponse> optFilmResponse = filmApiService.sendRequestByName(filmName);
        if (optFilmResponse.isEmpty()) {
            return Optional.empty();
        }
        //TODO FilmResponse cast to Film
        Film film = toFilm(filmResponse);
        filmDAO.saveFilm(film);
        return Optional.ofNullable(film);
    }

    private Film toFilm(FilmResponse filmResponse) {
        return null;
    }

}
