package ru.nsu.kinolist.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.database.DAO.FilmDAO;
import ru.nsu.kinolist.database.DAO.PersonDAO;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.service.FilmModificationHandler;
import ru.nsu.kinolist.utils.ListType;

import java.util.List;
import java.util.Random;

@Component
public class WishListController extends ListController {

    private final PersonDAO personDAO;


    @Autowired
    public WishListController(FilmDAO filmDAO, FilmModificationHandler filmModificationHandler, PersonDAO personDAO) {
          super(filmModificationHandler, filmDAO);
      //  this.filmModificationHandler = filmModificationHandler;
        this.personDAO = personDAO;
    }

    public Film getRandomFilmByUser(String chatId) {
        List<Film> films = personDAO.getAllFilmsByChatIdFromList(chatId, ListType.WISH);
        Random random = new Random();
        return films.get(random.nextInt(films.size()));
    }

}
