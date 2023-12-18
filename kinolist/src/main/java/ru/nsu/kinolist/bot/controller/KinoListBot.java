package ru.nsu.kinolist.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ru.nsu.kinolist.bot.util.Constants.*;

@Component
@Slf4j
public class KinoListBot extends TelegramLongPollingBot {
    private TelegramFacade telegramFacade;

    @Value("${bot.name}")
    private String botName;

    public KinoListBot(@Value("${bot.token}") String botToken, TelegramFacade telegramFacade) {
        super(botToken);
        this.telegramFacade = telegramFacade;

        this.addBotCommands();
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public void onUpdateReceived(Update update) {
        List<PartialBotApiMethod<? extends Serializable>> messages = telegramFacade.handleUpdate(update);

        messages.forEach(response -> {
            if (response instanceof SendMessage newResponse) {
                if (executeMessage(newResponse)) {
                    log.info("Message was sent successfully to User[{}] content: [{}]", newResponse.getChatId(), response);
                } else {
                    log.info("Message was not sent to User[{}], content: [{}]", newResponse.getChatId(), response);
                }
            } else if (response instanceof SendPhoto newResponse) {
                if (executeMessage(newResponse)) {
                    log.info("Photo was sent successfully to User[{}] content: [{}]", newResponse.getChatId(), response);
                } else {
                    log.info("Photo was not sent to User[{}], content: [{}]", newResponse.getChatId(), response);
                }
            } else if (response instanceof EditMessageText newResponse) {
                if (executeMessage(newResponse)) {
                    log.info("Message edit was successful for User[{}] content: [{}]", newResponse.getChatId(), response);
                } else {
                    log.info("Message edit was not successful for User[{}], content: [{}]", newResponse.getChatId(), response);
                }
            } else if (response instanceof EditMessageReplyMarkup newResponse) {
                if (executeMessage(newResponse)) {
                    log.info("Message reply markup edit was successful for User[{}] content: [{}]", newResponse.getChatId(), response);
                } else {
                    log.info("Message reply markup edit was not successful for User[{}], content: [{}]", newResponse.getChatId(), response);
                }
            }
        });
    }

    private void addBotCommands(){
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(new BotCommand(START_COMMAND, START_COMMAND_DESCRIPTION));
        listOfCommands.add(new BotCommand(MENU_COMMAND, MAIN_MENU_COMMAND_TEXT));
        listOfCommands.add(new BotCommand(HELP_COMMAND, HELP_COMMAND_DESCRIPTION));
        try {
            this.execute(new SetMyCommands(listOfCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            log.error(Arrays.toString(e.getStackTrace()));
        }
    }

    private boolean executeMessage(SendMessage message) {
        try {
            execute(message);
            return true;
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
            return false;
        }
    }

    private boolean executeMessage(SendPhoto message) {
        try {
            execute(message);
            return true;
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
            return false;
        }
    }

    private boolean executeMessage(EditMessageText message) {
        try {
            execute(message);
            return true;
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
            return false;
        }
    }

    private boolean executeMessage(EditMessageReplyMarkup message) {
        try {
            execute(message);
            return true;
        } catch (TelegramApiException e) {
            log.error("Ошибка отправки сообщения " + e.getMessage());
            return false;
        }
    }
}
