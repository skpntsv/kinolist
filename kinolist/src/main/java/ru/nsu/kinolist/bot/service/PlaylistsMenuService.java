package ru.nsu.kinolist.bot.service;

import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
public class PlaylistsMenuService {
    public List<PartialBotApiMethod<? extends Serializable>> getPlaylistMessage(Long chatId, Integer messageId) {
        EditMessageText editedMessage = new EditMessageText();
        editedMessage.setChatId(chatId);
        editedMessage.setMessageId(messageId);

        editedMessage.setText("Мои плейлисты: ");

        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Список желаемого", CallbackQueryType.WISHLIST.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Список просмотренного", CallbackQueryType.VIEWEDLIST.name())));
        rowsInline.add(Collections.singletonList(MessagesService.getButton("Список отслеживаемого", CallbackQueryType.TRACKEDLIST.name())));

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        EditMessageReplyMarkup editMessageReplyMarkup = new EditMessageReplyMarkup();
        editMessageReplyMarkup.setChatId(chatId);
        editMessageReplyMarkup.setMessageId(messageId);
        editMessageReplyMarkup.setReplyMarkup(inlineKeyboardMarkup);

        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        log.info("Cообщение [{}] успешно изменено у {}", messageId, chatId);
        messages.add(editedMessage);
        messages.add(editMessageReplyMarkup);
        return messages;
    }
}
