package ru.nsu.kinolist.bot.handlers.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.service.MainMenuService;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.WatchedListService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WatchedListQueryHandler implements CallbackQueryHandler {
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.WATCHEDLIST;

    private final WatchedListService watchedListService;
    private final UserDataCache userDataCache;
    private final MainMenuService mainMenuService;

    public WatchedListQueryHandler(WatchedListService watchedListService, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.watchedListService = watchedListService;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleCallbackQuery(CallbackQuery callbackQuery) {
        Long chatId = callbackQuery.getMessage().getChatId();

        if (!ParseQueryData.hasOperation(callbackQuery)) {
            return handlePlaylistCommand(chatId, callbackQuery);
        }

        String operation = ParseQueryData.parseOperation(callbackQuery);
        switch (operation) {
            case "ADD" -> {
                return handleAddOperation(chatId, callbackQuery);
            }
            case "REMOVE" -> {
                return handleRemoveOperation(chatId, callbackQuery);
            }
            default -> {
                log.error("Operation [{}] in callback [{}] not found from chatId [{}]", operation, callbackQuery.getMessage(), chatId);
                return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
            }
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> handlePlaylistCommand(Long chatId, CallbackQuery callbackQuery) {
        log.info("Send WATCHEDLIST menu to chatId [{}]", chatId);

        return watchedListService.getWatchedListMessage(chatId, callbackQuery.getMessage().getMessageId());
    }

    private List<PartialBotApiMethod<? extends Serializable>> handleAddOperation(Long chatId, CallbackQuery callbackQuery) {
        log.debug("Transaction Processing [ADD] in CallBackData [{}]", callbackQuery.getMessage());
        if (ParseQueryData.hasAck(callbackQuery)) {
            userDataCache.removeUsersCache(chatId);
            List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

            String ack = ParseQueryData.parseYesOrNo(callbackQuery);
            if (ack.equals("YES")) {
                if (watchedListService.addMovie(chatId)) {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно добавлен"));

                } else {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                }
            } else if (ack.equals("NO")) {
                log.debug("The chatId [{}] rejected adding a movie, show WATCHEDLIST again", callbackQuery.getMessage().getChatId());
                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не добавляем"));
            }
            resultMessages.addAll(mainMenuService.getMainMenuMessage(chatId));

            userDataCache.removeCurrentMovieListOfUser(chatId);
            return resultMessages;
        } else {
            userDataCache.setUsersCurrentBotState(chatId, BotState.WATCHEDLIST_ADD);

            return watchedListService.sendSearchMovie(chatId, callbackQuery.getMessage().getMessageId());
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> handleRemoveOperation(Long chatId, CallbackQuery callbackQuery) {
        log.debug("Transaction Processing [REMOVE] in CallBackData [{}]", callbackQuery.getMessage());
        if (ParseQueryData.hasAck(callbackQuery)) {
            userDataCache.removeUsersCache(chatId);
            List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

            String ack = ParseQueryData.parseYesOrNo(callbackQuery);
            if (ack.equals("YES")) {
                log.debug("Попытка удалить фильм[] из [WATCHEDLIST]");
                if (watchedListService.removeMovie(chatId)) {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно удален"));
                } else {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                }
            } else if (ack.equals("NO")) {
                log.debug("The chatId [{}] rejected adding a movie, show WATCHEDLIST again", callbackQuery.getMessage().getChatId());
                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не удаляем"));
            }
            resultMessages.addAll(mainMenuService.getMainMenuMessage(chatId));

            userDataCache.removeCurrentMovieListOfUser(chatId);
            return resultMessages;
        } else {
            userDataCache.setUsersCurrentBotState(chatId, BotState.WATCHEDLIST_REMOVE);

            return watchedListService.sendWriteIDMovie(chatId);
        }
    }
}
