package ru.nsu.kinolist.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;
import ru.nsu.kinolist.bot.util.FilmMessageBuilder;
import ru.nsu.kinolist.controllers.ListController;
import ru.nsu.kinolist.database.entities.Film;
import ru.nsu.kinolist.utils.ListType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class TrackedListService {
    private final ListController listController;
    private final UserDataCache userDataCache;

    public TrackedListService(ListController listController, UserDataCache userDataCache) {
        this.listController = listController;
        this.userDataCache = userDataCache;
    }

    public List<PartialBotApiMethod<? extends Serializable>> getTrackedListMessage(Long chatId, Integer messageId) {
        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        String formattedText = FilmMessageBuilder.formatFilmList(getTrackedList(chatId));
        if (formattedText.isEmpty()) {
            editedMessage.setText("Список пока что пуст");
        } else {
            editedMessage.setText(formattedText);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(Collections.singletonList(MessagesService.getButton("Добавить фильм/сериал",
                CallbackQueryType.TRACKEDLIST.name() + "|" + CallbackQueryType.ADD.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Удалить фильм/сериал",
                CallbackQueryType.TRACKEDLIST.name() + "|" + CallbackQueryType.REMOVE.name())));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        messages.add(editedMessage);
        messages.add(editMessageReplyMarkup);
        return messages;
    }

    public List<PartialBotApiMethod<? extends Serializable>> sendSearchMovie(Long chatId, Integer messageId) {
        return List.of(MessagesService.createMessageTemplate(chatId, "Напишите название фильма/сериала"));
    }

    public List<PartialBotApiMethod<? extends Serializable>> sendWriteIDMovie(Long chatId) {
        return List.of(MessagesService.createMessageTemplate(chatId, "Введите номер фильма"));
    }

    public Optional<Film> searchMovieByName(String movieName) {
        return listController.findFilmByName(movieName);
    }

    public boolean addMovie(Long chatId) {
        List<Film> movies = userDataCache.getAndRemoveCurrentMovieListOfUser(chatId);
        if (movies != null) {
            if (movies.size() == 1) {
                listController.addByUser(String.valueOf(chatId), movies.get(0), ListType.TRACKED);

                return true;
            }
        }

        return false; // получилось добавить или нет
    }

    public boolean removeMovie(Long chatId) {
        List<Film> movies = userDataCache.getAndRemoveCurrentMovieListOfUser(chatId);
        if (movies != null) {
            if (movies.size() == 1) {
                listController.removeByUser(String.valueOf(chatId), movies.get(0), ListType.TRACKED);

                return true;
            }
        }

        return false; // получилось удалить или нет
    }

    private List<Film> getTrackedList(Long chatId) {
        List<Film> movies = listController.showByUser(String.valueOf(chatId), ListType.TRACKED);
        userDataCache.setCurrentMovieListOfUser(chatId, movies);
        return movies;
    }
}
