package ru.nsu.kinolist.database.DAO;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.database.entities.Person;
import ru.nsu.kinolist.utils.ListType;

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
    public void save(Film film) throws CannotCreateTransactionException, GenericJDBCException, ConstraintViolationException {
        Session session = sessionFactory.getCurrentSession();
        session.save(film);
    }

    @Transactional
    public void saveByChatIdToList(String chatId, Film film, ListType listType) throws
            DataIntegrityViolationException, CannotCreateTransactionException, GenericJDBCException {
        Session session = sessionFactory.getCurrentSession();

        Person person = (Person) session.createQuery("from Person where chatId=:chatId")
                .setParameter("chatId", chatId).getSingleResult();

        switch (listType) {
            case WISH -> person.getWishList().add(film);
            case TRACKED -> person.getTrackedList().add(film);
            case VIEWED -> person.getViewedList().add(film);
        }
    }

    @Transactional(readOnly = true)
    public Optional<Film> getByName(String filmName) throws
            CannotCreateTransactionException, GenericJDBCException {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("from Film where lower(filmName) like :filmName")
                .setParameter("filmName", filmName.toLowerCase() + "%").getResultStream().findAny();
    }

    @Transactional(readOnly = true)
    public List<Film> getAllFilmsFromTracking() throws
            CannotCreateTransactionException, GenericJDBCException {
        Session session = sessionFactory.getCurrentSession();

        return session.createQuery("select distinct f from Film f join f.trackingPeople")
                .getResultList();
    }

    @Transactional(readOnly = true)
    public List<Person> getPeopleByTrackedFilm(Film film) throws
            CannotCreateTransactionException, GenericJDBCException {
        Session session = sessionFactory.getCurrentSession();

        film = (Film) session.merge(film);
        Hibernate.initialize(film.getTrackingPeople());

        return film.getTrackingPeople();
    }

    @Transactional
    public void deleteByChatIdFromList(String chatId, Film film, ListType listType) throws
            CannotCreateTransactionException, GenericJDBCException {
        Session session = sessionFactory.getCurrentSession();

        film = (Film) session.merge(film);
        Person person = (Person) session.createQuery("from Person where chatId=:chatId")
                .setParameter("chatId", chatId).getSingleResult();

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
