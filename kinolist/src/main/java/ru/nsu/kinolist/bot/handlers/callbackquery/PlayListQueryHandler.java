package ru.nsu.kinolist.bot.handlers.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.service.MainMenuService;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.PlayListService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class PlayListQueryHandler implements CallbackQueryHandler {
    protected MainMenuService mainMenuService;
    protected PlayListService playListService;
    protected UserDataCache userDataCache;

    public PlayListQueryHandler(PlayListService playListService, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.playListService = playListService;
        this.userDataCache = userDataCache;
        this.mainMenuService = mainMenuService;
    }
    protected List<PartialBotApiMethod<? extends Serializable>> handlePlaylistCommand(Long chatId, CallbackQuery callbackQuery) {
        log.info("Send TRACKEDLIST menu to chatId [{}]", chatId);

        return playListService.getListOfPlaylistMessage(chatId, callbackQuery.getMessage().getMessageId(), getHandlerQueryType());
    }

    protected List<PartialBotApiMethod<? extends Serializable>> handleAddOperation(Long chatId, CallbackQuery callbackQuery, BotState newBotState) {
        log.debug("Transaction Processing [ADD] in CallBackData [{}]", callbackQuery.getMessage());
        if (ParseQueryData.hasAck(callbackQuery)) {
            userDataCache.removeUsersCache(chatId);
            List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

            String ack = ParseQueryData.parseYesOrNo(callbackQuery);
            if (ack.equals("YES")) {
                if (playListService.addMovie(chatId, getHandlerQueryType())) {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно добавлен"));

                } else {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                }
            } else if (ack.equals("NO")) {
                log.debug("The chatId [{}] rejected adding a movie, show TRACKEDLIST again", callbackQuery.getMessage().getChatId());
                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не добавляем"));
            }
            resultMessages.addAll(mainMenuService.getMainMenuMessage(chatId));

            userDataCache.removeCurrentMovieListOfUser(chatId);
            return resultMessages;
        } else {
            userDataCache.setUsersCurrentBotState(chatId, newBotState);

            return playListService.sendSearchMovie(chatId, callbackQuery.getMessage().getMessageId());
        }
    }

    protected List<PartialBotApiMethod<? extends Serializable>> handleRemoveOperation(Long chatId, CallbackQuery callbackQuery, BotState newBotState) {
        log.debug("Transaction Processing [] in CallBackData [{}]", callbackQuery.getMessage());
        if (ParseQueryData.hasAck(callbackQuery)) {
            userDataCache.removeUsersCache(chatId);
            List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

            String ack = ParseQueryData.parseYesOrNo(callbackQuery);
            if (ack.equals("YES")) {
                log.debug("Попытка удалить фильм[] из []");
                if (playListService.removeMovie(chatId, getHandlerQueryType())) {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно удален"));
                } else {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                }
            } else if (ack.equals("NO")) {
                log.debug("The chatId [{}] rejected adding a movie, show  again", callbackQuery.getMessage().getChatId());
                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не удаляем"));
            }
            resultMessages.addAll(mainMenuService.getMainMenuMessage(chatId));

            userDataCache.removeCurrentMovieListOfUser(chatId);
            return resultMessages;
        } else {
            userDataCache.setUsersCurrentBotState(chatId, newBotState);

            return playListService.sendWriteIDMovie(chatId);
        }
    }
}
