package ru.nsu.kinolist.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.database.DAO.FilmDAO;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.filmApi.response.FilmResponse;
import ru.nsu.kinolist.filmApi.service.FilmApiService;

import java.util.Optional;

@Component
public class FilmModificationHandler {

    private final FilmApiService filmApiService;

    private final FilmDAO filmDAO;

    @Autowired
    public FilmModificationHandler(FilmApiService filmApiService, FilmDAO filmDAO) {
        this.filmApiService = filmApiService;
        this.filmDAO = filmDAO;
    }


    public Optional<Film> findFilm(String filmName) {
        Optional<Film> filmFromDB = filmDAO.getByName(filmName);
        if (filmFromDB.isPresent()) {
            return filmFromDB;
        }
        Optional<FilmResponse> optFilmResponse = filmApiService.sendRequestByName(filmName);
        if (optFilmResponse.isEmpty()) {
            return Optional.empty();
        }

        Film film = toFilm(optFilmResponse.get());
        filmDAO.save(film);
        return Optional.of(film);
    }

    private Film toFilm(FilmResponse filmResponse) {
        return new Film(filmResponse.getNameRu(), filmResponse.getYear(), filmResponse.getPosterUrl(), filmResponse.getFilmId(),
                !filmResponse.getType().equals("FILM"), filmResponse.getRating(), filmResponse.getDescription());
    }

}
