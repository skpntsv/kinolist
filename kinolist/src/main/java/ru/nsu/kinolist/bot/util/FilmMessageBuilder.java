package ru.nsu.kinolist.bot.util;

import ru.nsu.kinolist.database.entities.Film;

import java.util.List;

public class FilmMessageBuilder {
    public static String buildFilmString(Film film) {
        StringBuilder messageText = new StringBuilder();

        messageText.append(film.getFilmName()).append(" (").append(film.getReleaseYear()).append(")\n");

        if (film.getIsSeries()) {
            messageText.append("Тип: Сериал\n");
        } else {
            messageText.append("Тип: Фильм\n");
        }

        messageText.append("Рейтинг: ").append(film.getRating()).append("\n");
        messageText.append("Аннотация: ").append(film.getAnnotation()).append("\n");


        return messageText.toString();
    }

    public static String formatFilmList(List<Film> films) {
        StringBuilder formattedList = new StringBuilder();

        for (int i = 0; i < films.size(); i++) {
            Film film = films.get(i);
            formattedList.append(i + 1).append(". ").append(film.getFilmName()).append("\n");
        }

        return formattedList.toString();
    }
}