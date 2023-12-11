package ru.nsu.kinolist.bot.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.util.BotState;

import static ru.nsu.kinolist.bot.util.Constants.*;

@Service
@Slf4j
public class TelegramFacade {
    private final UserDataCache userDataCache;

    public TelegramFacade(UserDataCache userDataCache) {
        this.userDataCache = userDataCache;
    }

    public SendMessage handleUpdate(Update update) {
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {}, chatId {}, with data: {}",
                    update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getMessage().getChatId(),
                    update.getCallbackQuery().getData());
            return callbackQueryFacade.processCallbackQuery(update.getCallbackQuery());
        }


        Message message = update.getMessage();
        if (message != null && message.hasText()) {
            log.info("New message from chatIed:{}, chatId: {},  with text: {}",
                    message.getFrom().getUserName(),
                    message.getChatId(),
                    message.getText());
            replyMessage = handleInputMessage(message);
        }

        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message) {
        String inputMsg = message.getText();
        Long chatId = message.getChatId();
        BotState botState;
        SendMessage replyMessage;

        botState = switch (inputMsg) {
            case "/start" -> BotState.SHOW_MAIN_MENU;
            case "/help" -> BotState.SHOW_HELP;
            case MENU_COMMAND, MAIN_MENU_COMMAND_TEXT -> BotState.SHOW_MAIN_MENU;
            case SHOW_PLAYLISTS_COMMAND -> BotState.SHOW_PLAYLISTS_MENU;
            default -> userDataCache.getUsersCurrentBotState(chatId);
        };

        userDataCache.setUsersCurrentBotState(chatId, botState);

        //replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }


}
