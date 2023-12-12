package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.service.WishlistService;

import java.io.Serializable;
import java.util.List;

@Component
public class WishListQueryHandle implements CallbackQueryHandler{
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.WISHLIST;

    private WishlistService wishlistService;

    public WishListQueryHandle(WishlistService wishlistService) {
        this.wishlistService = wishlistService;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleCallbackQuery(CallbackQuery callbackQuery) {
        return wishlistService.getWishListMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }
}
