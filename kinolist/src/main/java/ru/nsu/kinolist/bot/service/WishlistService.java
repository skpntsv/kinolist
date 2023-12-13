package ru.nsu.kinolist.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;
import ru.nsu.kinolist.controllers.ListController;
import ru.nsu.kinolist.database.entities.Film;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
public class WishlistService {
    private final ListController listController;

    public WishlistService(ListController listController) {
        this.listController = listController;
    }

    public List<PartialBotApiMethod<? extends Serializable>> getWishListMessage(Long chatId, Integer messageId) {
        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        editedMessage.setText(getWishList(chatId));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(Collections.singletonList(MessagesService.getButton("Добавить фильм/сериал",
                CallbackQueryType.WISHLIST.name() + "|" + CallbackQueryType.ADD.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Перенести фильм/сериал",
                CallbackQueryType.WISHLIST.name() + "|" + CallbackQueryType.TRANSFER.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Удалить фильм/сериал из плейлиста",
                CallbackQueryType.WISHLIST.name() + "|" + CallbackQueryType.REMOVE.name())));

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

    public List<PartialBotApiMethod<? extends Serializable>> searchMovies(Long chatId, String movieName) {
        // TODO сделать поиск фильма и выдача соответствующего сообщения

        return null;
    }

    public List<PartialBotApiMethod<? extends Serializable>> sendWriteIDMovie(Long chatId) {
        return List.of(MessagesService.createMessageTemplate(chatId, "Введите номер фильма"));
    }

    public Optional<Film> searchMovieByName(String movieName) {
        return listController.findFilmByName(movieName);
    }

    public boolean addMovie(Long chatId, int filmId) {
        // TODO сделать добавление фильма


        return true; // получилось добавить или нет
    }

    public boolean transferMovie(Long chatId, int filmIdm) {
        // TODO сделать перенос фильма


        return true; // получилось перенести или нет
    }

    public boolean removeMovie(Long chatId, int filmId) {
        // TODO сделать удаление фильма из БД


        return true; // получилось удалить или нет
    }

    private String getWishList(Long ChatId) {
        // TODO нужно брать из БД список фильмов

        return """
                1. Матрица 4
                2. Побег из Шоушенко
                3. Рик и Морти (2013-н.в.)
                """;
    }
}
