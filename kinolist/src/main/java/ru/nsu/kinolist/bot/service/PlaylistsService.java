package ru.nsu.kinolist.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PlaylistsService {
    public List<PartialBotApiMethod<? extends Serializable>> getPlaylistMessage(Long chatId, Integer messageId) {
        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        editedMessage.setText("Мои плейлисты: ");

        String WatchedListCallbackData = String.format("%s|%s|%s", CallbackQueryType.WISHLIST.name(),
                "*", "*");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(Collections.singletonList(getButton("Список желаемого", CallbackQueryType.WISHLIST.name())));
        rowsInline.add(Collections.singletonList(getButton("Список просмотренного", CallbackQueryType.WATCHEDLIST.name())));
        rowsInline.add(Collections.singletonList(getButton("Список отслеживаемого", CallbackQueryType.TRACKEDLIST.name())));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();
//        try {
//            execute(editedMessage);
//            execute(editMessageReplyMarkup);
//        } catch (TelegramApiException e) {
//            log.error("Ошибка отправки сообщения " + e.getMessage());
//        }
//        log.info("Cообщение [{}] успешно изменено у {}", messageId, chatId);
        messages.add(editedMessage);
        messages.add(editMessageReplyMarkup);
        return messages;
    }

    private InlineKeyboardButton getButton(String nameButton, String callBackData){
        var button = new InlineKeyboardButton();

        button.setText(nameButton);
        button.setCallbackData(callBackData);

        return button;
    }
}
