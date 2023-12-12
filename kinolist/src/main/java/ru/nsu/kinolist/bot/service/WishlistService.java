package ru.nsu.kinolist.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class WishlistService {
    public List<PartialBotApiMethod<? extends Serializable>> getWishListMessage(Long chatId, Integer messageId) {
        //sendAction(chatId, ActionType.TYPING);

        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        editedMessage.setText(getWishList(chatId));

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        // TODO сделать, константы, поставить нормальные callbacks
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

    public List<PartialBotApiMethod<? extends Serializable>> sendACKMessage(Long chatId, Integer messageId) {
        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        messages.add(new SendMessage(chatId.toString(), "Напишите название фильма/сериала"));

        return messages;
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
