package ru.nsu.kinolist.bot.util;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nsu.kinolist.bot.util.CallbackQueryType;
import ru.nsu.kinolist.bot.util.ParseQueryData;

import java.util.ArrayList;
import java.util.List;

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

    public static InlineKeyboardMarkup createYesOrNoButton(CallbackQueryType playlist, CallbackQueryType operation) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        // Добавляем кнопку "Да"
        String callBackDataOnYes = ParseQueryData.createCallbackData(playlist.name(),
                operation.name(),
                CallbackQueryType.YES.name());
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
