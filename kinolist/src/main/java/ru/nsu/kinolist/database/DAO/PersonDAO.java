package ru.nsu.kinolist.database.DAO;

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
    public void savePerson(String chatId) {
        Session session = sessionFactory.getCurrentSession();
        Person person = new Person(chatId);
        session.save(person);
    }

    @Transactional(readOnly = true)
    public List<Film> getAllByUserIdFromList(int userId, ListType listType) {
        Session session = sessionFactory.getCurrentSession();
        Person person = session.get(Person.class, userId);
        List<Film> filmList = null;
        switch (listType) {
            case WISH -> filmList = person.getWishList();
            case TRACKED -> filmList = person.getTrackedList();
            case VIEWED -> filmList = person.getViewedList();
        }
        return filmList;
    }
}
