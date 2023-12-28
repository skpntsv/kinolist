package ru.nsu.kinolist.bot.handlers.playlists.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.playlists.PlayListQueryHandler;
import ru.nsu.kinolist.bot.util.CallbackQueryType;
import ru.nsu.kinolist.bot.util.ParseQueryData;
import ru.nsu.kinolist.bot.handlers.mainMenu.MainMenuService;
import ru.nsu.kinolist.bot.util.MessagesService;
import ru.nsu.kinolist.bot.handlers.playlists.PlayListService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
public class TrackedListQueryHandler extends PlayListQueryHandler {
    @Autowired
    public TrackedListQueryHandler(PlayListService playListService, UserDataCache userDataCache, MainMenuService mainMenuService) {
        super(playListService, userDataCache, mainMenuService);
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return CallbackQueryType.TRACKEDLIST;
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
                return handleAddOperation(chatId, callbackQuery, BotState.TRACKEDLIST_ADD);
            }
            case "REMOVE" -> {
                return handleRemoveOperation(chatId, callbackQuery, BotState.TRACKEDLIST_REMOVE);
            }
            default -> {
                log.error("Operation [{}] in callback [{}] not found from chatId [{}]", operation, callbackQuery.getMessage(), chatId);
                return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
            }
        }
    }
}
