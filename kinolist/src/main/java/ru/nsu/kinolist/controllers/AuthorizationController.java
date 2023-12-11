package ru.nsu.kinolist.controllers;

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

    public void registerNewUser(String chatId) {
        personDAO.save(chatId);
    }
}
