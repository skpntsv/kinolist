package ru.nsu.kinolist.bot.handlers.help;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.handlers.InputMessageHandler;
import ru.nsu.kinolist.bot.util.MessagesService;
import ru.nsu.kinolist.bot.util.BotState;
import ru.nsu.kinolist.bot.util.Constants;

import java.io.Serializable;
import java.util.List;

@Component
public class HelpHandler implements InputMessageHandler {

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleMessage(Message message) {
        return List.of(MessagesService.createMessageTemplate(message.getChatId(), Constants.HELP_MESSAGE));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP;
    }
}
