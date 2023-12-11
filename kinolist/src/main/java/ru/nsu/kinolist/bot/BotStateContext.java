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
    private Map<BotState, InputMessageHandler> messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public List<PartialBotApiMethod<? extends Serializable>> processInputMessage(BotState currentState, Message message) {
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);

        return currentMessageHandler.handleMessage(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState) {
//        if (isTrainSearchState(currentState)) {
//            return messageHandlers.get(BotState.TRAINS_SEARCH);
//        }
//
//        if (isStationSearchState(currentState)) {
//            return messageHandlers.get(BotState.STATIONS_SEARCH);
//        }
        if (currentState == BotState.SHOW_MAIN_MENU) {

        }

        return messageHandlers.get(currentState);
    }

    private boolean isTrainSearchState(BotState currentState) {
//        return switch (currentState) {
//            case TRAINS_SEARCH, ASK_DATE_DEPART, DATE_DEPART_RECEIVED, ASK_STATION_ARRIVAL, ASK_STATION_DEPART, TRAINS_SEARCH_STARTED, TRAIN_INFO_RESPONCE_AWAITING, TRAINS_SEARCH_FINISH ->
//                    true;
//            default -> false;
//        };
        return false;
    }

    private boolean isStationSearchState(BotState currentState) {
//        switch (currentState) {
//            case SHOW_STATIONS_BOOK_MENU:
//            case ASK_STATION_NAMEPART:
//            case STATION_NAMEPART_RECEIVED:
//            case STATIONS_SEARCH:
//                return true;
//            default:
//                return false;
//        }
        return false;
    }
}
