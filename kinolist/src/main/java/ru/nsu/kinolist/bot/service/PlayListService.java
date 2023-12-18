package ru.nsu.kinolist.bot.service;

import org.springframework.beans.factory.annotation.Autowired;
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
public class PlayListService {
    private ListController listController;
    protected UserDataCache userDataCache;
    @Autowired
    public PlayListService(ListController listController, UserDataCache userDataCache) {
        this.listController = listController;
        this.userDataCache = userDataCache;
    }

    public PlayListService() {
    }

    public List<PartialBotApiMethod<? extends Serializable>> getListOfPlaylistMessage(Long chatId, Integer messageId, CallbackQueryType playlist) {
        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        String formattedText = FilmMessageBuilder.formatFilmList(getPlayList(chatId, playlist));
        if (formattedText.isEmpty()) {
            editedMessage.setText("Список пока что пуст");
        } else {
            editedMessage.setText(formattedText);
        }

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(Collections.singletonList(MessagesService.getButton("Добавить фильм/сериал",
                playlist.name() + "|" + CallbackQueryType.ADD.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Удалить фильм/сериал",
                playlist.name() + "|" + CallbackQueryType.REMOVE.name())));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        return List.of(editedMessage, editMessageReplyMarkup);
    }

    public List<PartialBotApiMethod<? extends Serializable>> sendSearchMovie(Long chatId, Integer messageId) {
        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);
        editedMessage.setText("Напишите название фильма/сериала");

        return Collections.singletonList(editedMessage);
    }

    public List<PartialBotApiMethod<? extends Serializable>> sendWriteIDMovie(Long chatId) {
        return Collections.singletonList(MessagesService.createMessageTemplate(chatId, "Введите номер фильма"));
    }

    public Optional<Film> searchMovieByName(String movieName) {
        return listController.findFilmByName(movieName);
    }

    public boolean addMovie(Long chatId, CallbackQueryType playlistType) {
        List<Film> movies = userDataCache.getAndRemoveCurrentMovieListOfUser(chatId);
        if (movies != null) {
            if (movies.size() == 1) {
                int result = listController.addByUser(String.valueOf(chatId), movies.get(0), getListTypeByCallBack(playlistType));

                return result == 1;
            }
        }

        return false; // получилось добавить или нет
    }

    public boolean removeMovie(Long chatId, CallbackQueryType playlistType) {
        List<Film> movies = userDataCache.getAndRemoveCurrentMovieListOfUser(chatId);
        if (movies != null) {
            if (movies.size() == 1) {
                int result = listController.removeByUser(String.valueOf(chatId), movies.get(0), getListTypeByCallBack(playlistType));

                return result == 1;
            }
        }

        return false; // получилось удалить или нет
    }

    protected List<Film> getPlayList(Long chatId, CallbackQueryType playlistType) {
        List<Film> movies = listController.showByUser(String.valueOf(chatId), getListTypeByCallBack(playlistType));
        userDataCache.setCurrentMovieListOfUser(chatId, movies);
        return movies;
    }

    protected ListType getListTypeByCallBack(CallbackQueryType callbackQueryType) {
        return switch (callbackQueryType) {
            case WISHLIST -> ListType.WISH;
            case VIEWEDLIST -> ListType.VIEWED;
            case TRACKEDLIST -> ListType.TRACKED;
            default -> null;
        };
    }
}
