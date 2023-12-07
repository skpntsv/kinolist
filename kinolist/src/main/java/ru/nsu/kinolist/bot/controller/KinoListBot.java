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
    private static final String START = "/start";
    private static final String HELP = "/help";

    public KinoListBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update request) {
        if (request.hasMessage() && request.getMessage().hasText()) {
            log.info("Working onUpdateReceived, request text[{}] from @{}",
                    request.getMessage().getText(), request.getMessage().getChat().getUserName());
        } else {
            log.error("No message from {}", request.getMessage().getChat().getUserName());
            return;
        }

        String message = request.getMessage().getText();
        Long chatId = request.getMessage().getChatId();
        switch (message) {
            case START -> {
                String userName = request.getMessage().getChat().getFirstName();
                startCommand(chatId, userName);
            }
            case HELP -> helpCommand(chatId);
            default -> unknownCommand(chatId);
        }
    }

    private void startCommand(Long chatId, String userName) {
        String text = """
                **Добро пожаловать в бот, %s!**
                
                Здесь Вы сможете добавить фильм в wishlist.
                Поставить напоминание о выходе новой серии
                и не только...
                
                **Для этого воспользуйтесь командами:**
                // TODO добавить команды
                
                **Дополнительные команды:**
                /help - получение справки
                """;
        String formattedText = String.format(text, userName);

        sendMessage(chatId, formattedText);
    }

    private void helpCommand(Long chatId) {
        String text = """
                **Справочная информация по боту**
                
                **Для добавления фильма в список желаемого воспользуйтесь:**
                // TODO добавить команды
                """;
        sendMessage(chatId, text);
    }

    private void unknownCommand(Long chatId) {
        String text = "Не удалось распознать команду!\n" + "Введите /help";

        sendMessage(chatId, text);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);

        message.setChatId(String.valueOf(chatId));
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException ex) {
            log.error("Ошибка отправки сообщения " + ex.getMessage());
        }
        log.info("Сообщение [{}] отправлено {}", textToSend, chatId);
    }
}
