package ru.nsu.kinolist.bot.service;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class MessagesService {
    public static InlineKeyboardButton getButton(String nameButton, String callBackData){
        var button = new InlineKeyboardButton();

        button.setText(nameButton);
        button.setCallbackData(callBackData);

        return button;
    }
}
