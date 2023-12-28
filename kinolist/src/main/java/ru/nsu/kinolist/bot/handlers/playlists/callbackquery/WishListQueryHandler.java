package ru.nsu.kinolist.bot.handlers.playlists.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.playlists.PlayListQueryHandler;
import ru.nsu.kinolist.bot.handlers.playlists.WishListService;
import ru.nsu.kinolist.bot.util.CallbackQueryType;
import ru.nsu.kinolist.bot.util.ParseQueryData;
import ru.nsu.kinolist.bot.handlers.mainMenu.MainMenuService;
import ru.nsu.kinolist.bot.util.MessagesService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WishListQueryHandler extends PlayListQueryHandler {
    WishListService wishListService;
    public WishListQueryHandler(WishListService wishListService, UserDataCache userDataCache, MainMenuService mainMenuService) {
        super(wishListService, userDataCache, mainMenuService);
        this.wishListService = wishListService;
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return CallbackQueryType.WISHLIST;
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
                return handleAddOperation(chatId, callbackQuery, BotState.WISHLIST_ADD);
            }
            case "REMOVE" -> {
                return handleRemoveOperation(chatId, callbackQuery, BotState.WISHLIST_REMOVE);
            }
            case "TRANSFER" -> {
                return handleTransferOperation(chatId, callbackQuery);
            }
            default -> {
                log.error("Operation [{}] in callback [{}] not found from chatId [{}]", operation, callbackQuery.getMessage(), chatId);
                return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
            }
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> handleTransferOperation(Long chatId, CallbackQuery callbackQuery) {
        log.debug("Transaction Processing [TRANSFER] in CallBackData [{}]", callbackQuery.getMessage());
        if (ParseQueryData.hasAck(callbackQuery)) {
            userDataCache.removeUsersCache(chatId);
            List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

            String ack = ParseQueryData.parseYesOrNo(callbackQuery);
            if (ack.equals("YES")) {
                log.debug("Попытка переместить фильм[] в [VIEWEDLIST]");
                if (wishListService.transferMovie(chatId)) {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно перенесен в ваш список просмотренных"));
                } else {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                }
            } else if (ack.equals("NO")) {
                log.debug("The chatId [{}] rejected transferring a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());
                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не переносим"));
            }
            resultMessages.addAll(mainMenuService.getMainMenuMessage(chatId));

            userDataCache.removeCurrentMovieListOfUser(chatId);
            return resultMessages;
        } else {
            userDataCache.setUsersCurrentBotState(chatId, BotState.WISHLIST_TRANSFER);

            return wishListService.sendWriteIDMovie(chatId);
        }
    }
}
