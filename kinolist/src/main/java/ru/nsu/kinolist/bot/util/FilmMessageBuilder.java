package ru.nsu.kinolist.bot.util;

import ru.nsu.kinolist.database.entities.Film;

public class FilmMessageBuilder {
    public static String buildFilmMessage(Film film) {
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
}