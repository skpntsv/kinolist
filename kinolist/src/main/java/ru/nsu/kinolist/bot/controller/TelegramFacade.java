package ru.nsu.kinolist.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kinolist.bot.BotStateContext;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryFacade;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static ru.nsu.kinolist.bot.util.Constants.*;

@Service
@Slf4j
public class TelegramFacade {
    private final UserDataCache userDataCache;
    private final BotStateContext botStateContext;
    private final CallbackQueryFacade callbackQueryFacade;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext, CallbackQueryFacade callBackQueryFacade) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.callbackQueryFacade = callBackQueryFacade;
    }

    public List<PartialBotApiMethod<? extends Serializable>> handleUpdate(Update update) {
        List<PartialBotApiMethod<? extends Serializable>> replies = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {}, chatId {}, with data: {}",
                    update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getMessage().getChatId(),
                    update.getCallbackQuery().getData());

            return callbackQueryFacade.processCallbackQuery(update.getCallbackQuery());
        }

        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from chatIed: {}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(),
                    message.getChatId(),
                    message.getText());
            replies = handleInputMessage(message);
        }

        return replies;
    }

    private List<PartialBotApiMethod<? extends Serializable>> handleInputMessage(Message message) {
        String inputMsg = message.getText();
        Long chatId = message.getChatId();
        BotState botState;

        botState = switch (inputMsg) {
            case "/start" -> BotState.SHOW_MAIN_MENU;
            case "/help" -> BotState.SHOW_HELP;
            case MENU_COMMAND, MAIN_MENU_COMMAND_TEXT -> BotState.SHOW_MAIN_MENU;
            case SHOW_PLAYLISTS_COMMAND -> BotState.SHOW_PLAYLISTS_MENU;
            default -> userDataCache.getUsersCurrentBotState(chatId);
        };

        userDataCache.setUsersCurrentBotState(chatId, botState);

        return botStateContext.processInputMessage(botState, message);
    }

//    private void onCallbackQueryReceived(CallbackQuery callbackQuery) {
//        String data = callbackQuery.getData();
//        Integer messageId = callbackQuery.getMessage().getMessageId();
//        Long chatId = callbackQuery.getMessage().getChatId();
//
//        switch (data) {
//            case MAIN_MENU_COMMAND_TEXT -> showMainMenu(chatId);
//            case WISHLIST -> showWishList(chatId, messageId);
//            case WATCHED_LIST -> showWatchedList(chatId, messageId);
//            case TRACKED_LIST -> showTrackedList(chatId, messageId);
//            case PLAYLISTS_COMMAND_TEXT -> showPlaylists(chatId, messageId);
//            case ADD_WATCHEDLIST_COMMAND_TEXT -> requestUserInput(chatId);
//
//            default -> sendMessage(chatId, "пук-пук");
//        }
//    }
}
