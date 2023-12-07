package ru.nsu.kinolist.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
public class KinoListBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;

    public KinoListBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        var originalMessage = update.getMessage();
        log.info("Получено сообщение от @" +
                originalMessage.getChat().getUserName() + ":" + originalMessage.getText());

        sendMessage(originalMessage.getChatId(), "Hello my dear friend");
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error(ex.getMessage());
        }
    }
}
