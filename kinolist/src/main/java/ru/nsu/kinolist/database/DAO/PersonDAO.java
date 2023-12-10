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

@Component
public class PersonDAO {
    private final SessionFactory sessionFactory;

    @Autowired
    public PersonDAO(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Transactional
    public void save(String chatId) {
        Session session = sessionFactory.getCurrentSession();

        Person person = new Person(chatId);
        session.save(person);
    }

    @Transactional(readOnly = true)
    public List<Film> getAllByUserIdFromList(String chatId, ListType listType) {
        Session session = sessionFactory.getCurrentSession();

        Person person = (Person) session.createQuery("from Person where chatId=:chatId")
                .setParameter("chatId", chatId).getSingleResult();
        List<Film> filmList = null;

        switch (listType) {
            case WISH -> {
                Hibernate.initialize(person.getWishList());
                filmList = person.getWishList();
            }
            case TRACKED -> {
                Hibernate.initialize(person.getTrackedList());
                filmList = person.getTrackedList();
            }
            case VIEWED -> {
                Hibernate.initialize(person.getViewedList());
                filmList = person.getViewedList();
            }
        }

        return filmList;
    }
}
