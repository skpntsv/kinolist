package ru.nsu.kinolist.database.DAO;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.kinolist.ListType;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.database.entities.Person;

@Component
public class FilmDAO {
    private final SessionFactory sessionFactory;

    @Autowired
    public FilmDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void save(Film film) {
        Session session = sessionFactory.getCurrentSession();
        session.save(film);
    }

    @Transactional(readOnly = true)
    public void saveByIdToList(int userId, int filmId, ListType listType) {
        Session session = sessionFactory.getCurrentSession();
        Person person = session.get(Person.class, userId);
        Film film = session.get(Film.class, filmId);
        switch (listType) {
            case WISH -> {
                person.getWishList().add(film);
                film.getWishingPeople().add(person);
            }
            case TRACKED -> {
                person.getTrackedList().add(film);
                film.getTrackingPeople().add(person);
            }
            case VIEWED -> {
                person.getViewedList().add(film);
                film.getPeopleWhoViewed().add(person);
            }
        }
    }

    @Transactional(readOnly = true)
    public void deleteByIdFromList(int userId, int filmId, ListType listType) {
        Session session = sessionFactory.getCurrentSession();
        Person person = session.get(Person.class, userId);
        Film film = session.get(Film.class, filmId);
        switch (listType) {
            case WISH -> {
                person.getWishList().remove(film);
                film.getWishingPeople().remove(person);
            }
            case TRACKED -> {
                person.getTrackedList().remove(film);
                film.getTrackingPeople().remove(person);
            }
            case VIEWED -> {
                person.getViewedList().remove(film);
                film.getPeopleWhoViewed().remove(person);
            }
        }
    }
}
