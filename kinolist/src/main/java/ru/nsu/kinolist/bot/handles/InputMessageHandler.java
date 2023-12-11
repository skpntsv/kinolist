package ru.nsu.kinolist.bot.handles;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.util.BotState;

public interface InputMessageHandler {
    SendMessage handle(Message message);
    BotState getHandlerName();
}
