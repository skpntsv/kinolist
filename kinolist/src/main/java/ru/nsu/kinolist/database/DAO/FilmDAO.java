package ru.nsu.kinolist.database.DAO;

import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;
import org.springframework.transaction.annotation.Transactional;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.database.entities.Person;
import ru.nsu.kinolist.utils.ListType;

import java.util.ArrayList;
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
            ConstraintViolationException, CannotCreateTransactionException, GenericJDBCException {
        Session session = sessionFactory.getCurrentSession();

        Person person = (Person) session.createQuery("from Person where chatId=:chatId")
                .setParameter("chatId", chatId).getSingleResult();

        switch (listType) {
            case WISH -> {
                if (person.getWishList() == null) {
                    person.setWishList(new ArrayList<>());
                }
                person.getWishList().add(film);

                if (film.getWishingPeople() == null) {
                    film.setWishingPeople(new ArrayList<>());
                }
                film.getWishingPeople().add(person);
            }
            case TRACKED -> {
                if (person.getTrackedList() == null) {
                    person.setTrackedList(new ArrayList<>());
                }
                person.getTrackedList().add(film);

                if (film.getTrackingPeople() == null) {
                    film.setTrackingPeople(new ArrayList<>());
                }
                film.getTrackingPeople().add(person);
            }
            case VIEWED -> {
                if (person.getViewedList() == null) {
                    person.setViewedList(new ArrayList<>());
                }
                person.getViewedList().add(film);

                if (film.getPeopleWhoViewed() == null) {
                    film.setPeopleWhoViewed(new ArrayList<>());
                }
                film.getPeopleWhoViewed().add(person);
            }
        }
    }

    @Transactional(readOnly = true)
    public Optional<Film> getByName(String filmName) throws
            CannotCreateTransactionException, GenericJDBCException {
        Session session = sessionFactory.getCurrentSession();
        return session.createQuery("from Film where filmName like :filmName")
                .setParameter("filmName", filmName+"%").getResultStream().findAny();
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

        Hibernate.initialize(film.getTrackingPeople());
        return film.getTrackingPeople();
    }

    @Transactional
    public void deleteByChatIdFromList(String chatId, Film film, ListType listType) throws
            CannotCreateTransactionException, GenericJDBCException {
        Session session = sessionFactory.getCurrentSession();

        Person person = (Person) session.createQuery("from Person where chatId=:chatId")
                .setParameter("chatId", chatId).getSingleResult();

        switch (listType) {
            case WISH -> {
                Hibernate.initialize(person.getWishList());
                person.getWishList().remove(film);
                Hibernate.initialize(film.getWishingPeople());
                film.getWishingPeople().remove(person);
            }
            case TRACKED -> {
                Hibernate.initialize(person.getTrackedList());
                person.getTrackedList().remove(film);
                Hibernate.initialize(film.getTrackingPeople());
                film.getTrackingPeople().remove(person);
            }
            case VIEWED -> {
                Hibernate.initialize(person.getViewedList());
                person.getViewedList().remove(film);
                Hibernate.initialize(film.getPeopleWhoViewed());
                film.getPeopleWhoViewed().remove(person);
            }
        }
    }
}
