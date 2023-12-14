package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.nsu.kinolist.controllers.RandomFilmController;
import ru.nsu.kinolist.filmApi.response.Categories;
import ru.nsu.kinolist.filmApi.response.FilmResponseByRandom;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Component
public class RandomQueryHandler implements CallbackQueryHandler {
    private final RandomFilmController randomFilmController;

    public RandomQueryHandler(RandomFilmController randomFilmController) {
        this.randomFilmController = randomFilmController;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleCallbackQuery(CallbackQuery callbackQuery) {
        FilmResponseByRandom filmResponseByRandom = randomFilmController.getRandomFilm(new Categories());

        return sendRandomFilm(callbackQuery.getMessage().getChatId(), filmResponseByRandom);
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return CallbackQueryType.RANDOM;
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendRandomFilm(Long chatId, FilmResponseByRandom movie) {
        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        // Добавление постера
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId);
        sendPhoto.setPhoto(new InputFile(movie.getPosterUrl()));
        messages.add(sendPhoto);

        // Добавления описания
        StringBuilder messageText = new StringBuilder();
        messageText.append("Фильм: ").append(movie.getNameRu()).append(" (").append(movie.getYear()).append(")\n");
        messageText.append("Жанры: ").append(movie.getGenres()).append("\n");
        messageText.append("Рейтинг (Kinopoisk): ").append(movie.getRatingKinopoisk()).append("\n");
        messageText.append("Рейтинг (IMDb): ").append(movie.getRatingImdb()).append("\n");
        messageText.append("Тип: ").append(movie.getType()).append("\n");

        // Создаем текстовое сообщение и добавляем его в список сообщений
        SendMessage message = new SendMessage();
        message.setText(messageText.toString());
        message.setChatId(chatId);
        messages.add(message);

        return messages;
    }
}
