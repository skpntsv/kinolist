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
import ru.nsu.kinolist.controllers.WishListController;
import ru.nsu.kinolist.database.entities.Film;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WishListService extends PlayListService {
    private final WishListController wishListController;
    @Autowired
    public WishListService(WishListController wishListController, UserDataCache userDataCache) {
        super(wishListController, userDataCache);
        this.wishListController = wishListController;
    }

    @Override
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
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Перенести фильм/сериал в просмотренное",
                playlist.name() + "|" + CallbackQueryType.TRANSFER.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Удалить фильм/сериал из плейлиста",
                playlist.name() + "|" + CallbackQueryType.REMOVE.name())));

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

    public boolean transferMovie(Long chatId) {
        List<Film> movies = userDataCache.getAndRemoveCurrentMovieListOfUser(chatId);
        if (movies != null) {
            if (movies.size() == 1) {
                int result = wishListController.moveToViewedListByUser(String.valueOf(chatId), movies.get(0));

                return result == 1;
            }
        }

        return true; // получилось перенести или нет
    }
}
