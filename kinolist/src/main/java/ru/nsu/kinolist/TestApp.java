package ru.nsu.kinolist;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import ru.nsu.kinolist.database.DAO.FilmDAO;
import ru.nsu.kinolist.database.DAO.PersonDAO;
import ru.nsu.kinolist.database.entities.Film;

import java.util.List;

@SpringBootApplication
public class TestApp {
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(TestApp.class);
        PersonDAO personDAO = context.getBean("personDAO", PersonDAO.class);
        FilmDAO filmDAO = context.getBean("filmDAO", FilmDAO.class);
        List<Film> films = personDAO.getAllByUserIdFromList(1, ListType.WISH);
        for(Film film:films){
            System.out.println(film);
        }
    }
}
