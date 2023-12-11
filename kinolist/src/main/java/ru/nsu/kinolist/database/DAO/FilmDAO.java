package ru.nsu.kinolist.database.DAO;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.kinolist.ListType;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.database.entities.Person;

import java.util.List;
import java.util.Optional;

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

    @Transactional
    public void saveByChatIdToList(String chatId, int filmId, ListType listType) {
        Session session = sessionFactory.getCurrentSession();

        Person person = (Person) session.createQuery("from Person where chatId=:chatId")
                .setParameter("chatId", chatId).getSingleResult();
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
    public Optional<Film> getByName(String filmName) {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Film where filmName=:filmName")
                .setParameter("filmName", filmName).getResultStream().findAny();
    }

    @Transactional(readOnly = true)
    public List<Film> getAllFilmsFromTracking() {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("select distinct f from Film f join f.trackingPeople")
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Person> getPeopleByTrackedFilm(Film film) {
        Session session = sessionFactory.getCurrentSession();

        Hibernate.initialize(film.getTrackingPeople());
        return film.getTrackingPeople();
    }

    @Transactional
    public void deleteByChatIdFromList(String chatId, int filmId, ListType listType) {
        Session session = sessionFactory.getCurrentSession();

        Person person = (Person) session.createQuery("from Person where chatId=:chatId")
                .setParameter("chatId", chatId).getSingleResult();
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
