package ru.nsu.kinolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.database.DAO.FilmDAO;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.service.FilmModificationHandler;
import ru.nsu.kinolist.utils.ListType;

import java.util.List;
import java.util.Optional;

@Component
public class ListController {
    private final FilmModificationHandler filmModificationHandler;

    private final FilmDAO filmDAO;

    @Autowired
    public ListController(FilmModificationHandler filmModificationHandler, FilmDAO filmDAO) {
        this.filmModificationHandler = filmModificationHandler;
        this.filmDAO = filmDAO;
    }

    public Optional<Film> findFilmByName(String filmName) {
        return filmModificationHandler.findFilm(filmName);
    }

    void addByUser(String chatId, Film film, ListType listType) {
        filmDAO.saveByChatIdToList(chatId, film, listType);
    }

    void removeByUser(String chatId, Film film, ListType listType) {
        filmDAO.deleteByChatIdFromList(chatId, film, listType);
    }
}
