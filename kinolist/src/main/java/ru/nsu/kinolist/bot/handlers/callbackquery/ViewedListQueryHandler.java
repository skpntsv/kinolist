package ru.nsu.kinolist.bot.handlers.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.service.MainMenuService;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.PlayListService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
public class ViewedListQueryHandler extends PlayListQueryHandler {
    public ViewedListQueryHandler(PlayListService playListService, UserDataCache userDataCache, MainMenuService mainMenuService) {
        super(playListService, userDataCache, mainMenuService);
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return CallbackQueryType.VIEWEDLIST;
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
                return handleAddOperation(chatId, callbackQuery, BotState.VIEWEDLIST_ADD);
            }
            case "REMOVE" -> {
                return handleRemoveOperation(chatId, callbackQuery, BotState.VIEWEDLIST_REMOVE);
            }
            default -> {
                log.error("Operation [{}] in callback [{}] not found from chatId [{}]", operation, callbackQuery.getMessage(), chatId);
                return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
            }
        }
    }
}
