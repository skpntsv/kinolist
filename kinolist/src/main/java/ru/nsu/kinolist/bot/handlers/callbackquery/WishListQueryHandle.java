package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.service.WishlistService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Component
public class WishListQueryHandle implements CallbackQueryHandler{
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.WISHLIST;

    private WishlistService wishlistService;
    private UserDataCache userDataCache;

    public WishListQueryHandle(WishlistService wishlistService, UserDataCache userDataCache) {
        this.wishlistService = wishlistService;
        this.userDataCache = userDataCache;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleCallbackQuery(CallbackQuery callbackQuery) {
        if (!callbackQuery.getData().equals(CallbackQueryType.WISHLIST.name())){
            if (Objects.equals(ParseQueryData.parseOperation(callbackQuery), CallbackQueryType.ADD.name())) {
                userDataCache.setUsersCurrentBotState(callbackQuery.getMessage().getChatId(), BotState.WISHLIST_ADD);
                return wishlistService.sendACKMessage(callbackQuery.getMessage().getChatId(),
                        callbackQuery.getMessage().getMessageId());
            }

            return null;
        }
        userDataCache.setUsersCurrentBotState(callbackQuery.getMessage().getChatId(), BotState.IDLE);
        return wishlistService.getWishListMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }
}
