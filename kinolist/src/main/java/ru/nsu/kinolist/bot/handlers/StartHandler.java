package ru.nsu.kinolist.bot.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.util.BotState;
import ru.nsu.kinolist.bot.util.Constants;
import ru.nsu.kinolist.controllers.AuthorizationController;

import java.io.Serializable;
import java.util.List;

@Component
public class StartHandler implements InputMessageHandler {
    private final AuthorizationController authorizationController;

    public StartHandler(AuthorizationController authorizationController) {
        this.authorizationController = authorizationController;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleMessage(Message message) {
        int success = authorizationController.registerNewUser(message.getChatId().toString());
        if (success == 1) {
            return List.of(MessagesService.createMessageTemplate(message.getChatId(), String.format(Constants.START_MESSAGE_NEW_USER, message.getChat().getFirstName())));
        } else {
            return List.of(MessagesService.createMessageTemplate(message.getChatId(), Constants.START_MESSAGE_OLD_USER));
        }
    }

    @Override
    public BotState getHandlerName() {
        return BotState.START;
    }
}
