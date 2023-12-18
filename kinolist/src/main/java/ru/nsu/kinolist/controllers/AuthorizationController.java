package ru.nsu.kinolist.controllers;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.nsu.kinolist.database.DAO.PersonDAO;

@Component
public class AuthorizationController {
    private final PersonDAO personDAO;

    @Autowired
    public AuthorizationController(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    public int registerNewUser(String chatId) {
        try {
            personDAO.save(chatId);
            return 1;
        }
        catch (ConstraintViolationException e) {
            return 0;
        }
    }
}
