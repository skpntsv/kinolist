package ru.nsu.kinolist.bot.handlers;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.List;

public interface InputMessageHandler {
    List<PartialBotApiMethod<? extends Serializable>> handleMessage(Message message);
    BotState getHandlerName();
}
