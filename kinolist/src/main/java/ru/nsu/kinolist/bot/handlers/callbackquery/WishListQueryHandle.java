package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.Serializable;
import java.util.List;

public class WishListQueryHandle implements CallbackQueryHandler{
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.WISHLIST;
    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleCallbackQuery(CallbackQuery callbackQuery) {
        return null;
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }
}
