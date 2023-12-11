package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.Serializable;
import java.util.List;

public interface CallbackQueryHandler {
    List<PartialBotApiMethod<? extends Serializable>> handleCallbackQuery(CallbackQuery callbackQuery);

    CallbackQueryType getHandlerQueryType();
}
