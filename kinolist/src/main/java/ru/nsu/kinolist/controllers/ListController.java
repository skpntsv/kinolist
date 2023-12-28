package ru.nsu.kinolist.controllers;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.database.DAO.FilmDAO;
import ru.nsu.kinolist.database.DAO.PersonDAO;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.service.FilmModificationHandler;
import ru.nsu.kinolist.utils.ListType;

import java.util.List;
import java.util.Optional;

@Component
public class ListController { //@Qualifier("listController") для однозначности
    protected final FilmModificationHandler filmModificationHandler;

    protected final FilmDAO filmDAO;

    protected final PersonDAO personDAO;

    @Autowired
    public ListController(FilmModificationHandler filmModificationHandler, FilmDAO filmDAO, PersonDAO personDAO) {
        this.filmModificationHandler = filmModificationHandler;
        this.filmDAO = filmDAO;
        this.personDAO = personDAO;
    }

    public Optional<Film> findFilmByName(String filmName) {
        return filmModificationHandler.findFilm(filmName);
    }

    public int addByUser(String chatId, Film film, ListType listType) {
        try {
            filmDAO.saveByChatIdToList(chatId, film, listType);
            return 1;
        }
        catch (DataIntegrityViolationException e) {
            return 0;
        }
    }

    public int removeByUser(String chatId, Film film, ListType listType) {
        try {
            filmDAO.deleteByChatIdFromList(chatId, film, listType);
            return 1;
        }
        catch (ConstraintViolationException e) {
            return 0;
        }
    }

    public List<Film> showByUser(String chatId, ListType listType) {
        return personDAO.getAllFilmsByChatIdFromList(chatId, listType);
    }
}
