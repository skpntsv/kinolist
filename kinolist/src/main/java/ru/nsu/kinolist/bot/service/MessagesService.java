package ru.nsu.kinolist.bot.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;
import ru.nsu.kinolist.bot.handlers.callbackquery.ParseQueryData;
import ru.nsu.kinolist.database.entities.Film;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MessagesService {
    public static InlineKeyboardButton getButton(String nameButton, String callBackData){
        InlineKeyboardButton button = new InlineKeyboardButton();

        button.setText(nameButton);
        button.setCallbackData(callBackData);

        return button;
    }

    public static SendMessage createMessageTemplate(String chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        sendMessage.setText(text);

        return sendMessage;
    }

    public static SendMessage createMessageTemplate(Long chatId, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        sendMessage.setText(text);

        return sendMessage;
    }

    public static InlineKeyboardMarkup createYesOrNoButton(CallbackQueryType playlist, CallbackQueryType operation, int filmId) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Добавляем кнопку "Да"
        String callBackDataOnYes = ParseQueryData.createCallbackData(playlist.name(),
                operation.name(),
                CallbackQueryType.YES.name(),
                String.valueOf(filmId));
        List<InlineKeyboardButton> rowInlineYes = new ArrayList<>();
        rowInlineYes.add(getButton("Да", callBackDataOnYes));
        rowsInline.add(rowInlineYes);

        // Добавляем кнопку "Нет"
        String callBackDataOnNo = ParseQueryData.createCallbackData(playlist.name(),
                operation.name(),
                CallbackQueryType.NO.name());
        List<InlineKeyboardButton> rowInlineNo = new ArrayList<>();
        rowInlineYes.add(getButton("Нет", callBackDataOnNo));
        rowsInline.add(rowInlineNo);

        inlineKeyboardMarkup.setKeyboard(rowsInline);

        return inlineKeyboardMarkup;
    }

    public static SendMessage errorMessage(Long chatId) {
        return createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз");
    }
}
