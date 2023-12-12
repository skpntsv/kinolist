package ru.nsu.kinolist.bot;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.handlers.InputMessageHandler;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {
    private final Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public List<PartialBotApiMethod<? extends Serializable>> processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);

        return currentMessageHandler.handleMessage(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
        if (isTrackedListState(currentState)) {
            return messageHandlers.get(BotState.TRACKEDLIST);
        }
        if (isWatchedState(currentState)) {
            return messageHandlers.get(BotState.WATCHEDLIST);
        }
        if (isWishlistState(currentState)) {
            return messageHandlers.get(BotState.WISHLIST);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isWishlistState(BotState currentState) {
        return switch (currentState) {
            case WISHLIST_ADD, WISHLIST_REMOVE, WISHLIST_TRANSFER -> true;
            default -> false;
        };
    }
    private boolean isTrackedListState(BotState currentState) {
        return switch (currentState) {
            case TRACKED_REMOVE, TRACKEDLIST_ADD -> true;
            default -> false;
        };
    }
    private boolean isWatchedState(BotState currentState) {
        return switch (currentState) {
            case WATCHEDLIST_ADD, WATCHEDLIST_REMOVE -> true;
            default -> false;
        };
    }
}
