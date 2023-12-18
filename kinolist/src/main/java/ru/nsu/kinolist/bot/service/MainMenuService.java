package ru.nsu.kinolist.bot.service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;
import ru.nsu.kinolist.bot.handlers.callbackquery.ParseQueryData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static ru.nsu.kinolist.bot.util.Constants.*;

@Service
public class MainMenuService {
    public List<PartialBotApiMethod<? extends Serializable>> getMainMenuMessage(Long chatId) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);
        message.setChatId(chatId);
        message.setText("Главное меню:");

        message.setReplyMarkup(createMainInlineButtons());


        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();
        messages.add(message);

        return messages;
    }

    private InlineKeyboardMarkup createMainInlineButtons() {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        rowsInline.add(Collections.singletonList(MessagesService.getButton(PLAYLISTS_COMMAND_TEXT,
                ParseQueryData.createCallbackData(CallbackQueryType.SHOW_PLAYLIST.name()))));

        rowsInline.add(Collections.singletonList(MessagesService.getButton(ADD_WISHLIST_COMMAND_TEXT,
                ParseQueryData.createCallbackData(CallbackQueryType.WISHLIST.name(), CallbackQueryType.ADD.name()))));

        rowsInline.add(Collections.singletonList(MessagesService.getButton(ADD_WATCHEDLIST_COMMAND_TEXT,
                ParseQueryData.createCallbackData(CallbackQueryType.VIEWEDLIST.name(), CallbackQueryType.ADD.name()))));

        rowsInline.add(Collections.singletonList(MessagesService.getButton(ADD_TRACKEDLIST_COMMAND_TEXT,
                ParseQueryData.createCallbackData(CallbackQueryType.TRACKEDLIST.name(), CallbackQueryType.ADD.name()))));

        rowsInline.add(Collections.singletonList(MessagesService.getButton(SEARCH_RANDOM_COMMAND_TEXT,
                ParseQueryData.createCallbackData(CallbackQueryType.RANDOM.name()))));

        inlineKeyboardMarkup.setKeyboard(rowsInline);


        return inlineKeyboardMarkup;
    }
}
