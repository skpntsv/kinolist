package ru.nsu.kinolist.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class KinoListBot extends TelegramLongPollingBot {

    @Value("${bot.name}")
    private String botName;
    private static final String START = "/start";
    private static final String HELP = "/help";

    public KinoListBot(@Value("${bot.token}") String botToken) {
        super(botToken);

        this.addBotCommands();
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

    private void addBotCommands(){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand("/start", "get a welcome message"));
        listOfCommands.add(new BotCommand("/help", "info how to use this bot"));
        listOfCommands.add(new BotCommand("/add_film", "add film"));
        listOfCommands.add(new BotCommand("/status", "get status"));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
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
        String text = """
                Не удалось распознать команду!
                "Введите команда /help"
                """;

        sendMessage(chatId, text);
    }

    private void sendMessage(long chatId, String textToSend) {
        SendMessage message = new SendMessage();
        message.enableMarkdown(true);

        message.setChatId(chatId);
        message.setText(textToSend);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
        }
        log.info("Сообщение [{}] отправлено {}", textToSend, chatId);
    }
}
