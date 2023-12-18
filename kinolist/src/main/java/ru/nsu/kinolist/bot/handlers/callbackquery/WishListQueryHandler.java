package ru.nsu.kinolist.bot.handlers.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.service.MainMenuService;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.WishListService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WishListQueryHandler implements CallbackQueryHandler {
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.WISHLIST;

    private final WishListService wishListService;
    private final UserDataCache userDataCache;
    private final MainMenuService mainMenuService;

    public WishListQueryHandler(WishListService wishListService, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.wishListService = wishListService;
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
            case "TRANSFER" -> {
                return handleTransferOperation(chatId, callbackQuery);
            }
            default -> {
                log.error("Operation [{}] in callback [{}] not found from chatId [{}]", operation, callbackQuery.getMessage(), chatId);
                return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
            }
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> handlePlaylistCommand(Long chatId, CallbackQuery callbackQuery) {
        log.info("Send WISHLIST menu to chatId [{}]", chatId);

        return wishListService.getListOfPlaylistMessage(chatId, callbackQuery.getMessage().getMessageId(), getHandlerQueryType());
    }

    private List<PartialBotApiMethod<? extends Serializable>> handleAddOperation(Long chatId, CallbackQuery callbackQuery) {
        log.debug("Transaction Processing [ADD] in CallBackData [{}]", callbackQuery.getMessage());
        if (ParseQueryData.hasAck(callbackQuery)) {
            userDataCache.removeUsersCache(chatId);
            List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

            String ack = ParseQueryData.parseYesOrNo(callbackQuery);
            if (ack.equals("YES")) {
                if (wishListService.addMovie(chatId, getHandlerQueryType())) {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно добавлен"));

                } else {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                }
            } else if (ack.equals("NO")) {
                log.debug("The chatId [{}] rejected adding a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());
                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не добавляем"));
            }
            resultMessages.addAll(mainMenuService.getMainMenuMessage(chatId));

            userDataCache.removeCurrentMovieListOfUser(chatId);
            return resultMessages;
        } else {
            userDataCache.setUsersCurrentBotState(chatId, BotState.WISHLIST_ADD);

            return wishListService.sendSearchMovie(chatId, callbackQuery.getMessage().getMessageId());
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> handleRemoveOperation(Long chatId, CallbackQuery callbackQuery) {
        log.debug("Transaction Processing [REMOVE] in CallBackData [{}]", callbackQuery.getMessage());
        if (ParseQueryData.hasAck(callbackQuery)) {
            userDataCache.removeUsersCache(chatId);
            List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

            String ack = ParseQueryData.parseYesOrNo(callbackQuery);
            if (ack.equals("YES")) {
                log.debug("Попытка удалить фильм[] из [WISHLIST]");
                if (wishListService.removeMovie(chatId, getHandlerQueryType())) {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно удален"));
                } else {
                    resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                }
            } else if (ack.equals("NO")) {
                log.debug("The chatId [{}] rejected adding a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());
                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не удаляем"));
            }
            resultMessages.addAll(mainMenuService.getMainMenuMessage(chatId));

            userDataCache.removeCurrentMovieListOfUser(chatId);
            return resultMessages;
        } else {
            userDataCache.setUsersCurrentBotState(chatId, BotState.WISHLIST_REMOVE);

            return wishListService.sendWriteIDMovie(chatId);
        }
    }

    private List<PartialBotApiMethod<? extends Serializable>> handleTransferOperation(Long chatId, CallbackQuery callbackQuery) {
        log.debug("Transaction Processing [TRANSFER] in CallBackData [{}]", callbackQuery.getMessage());
        if (ParseQueryData.hasAck(callbackQuery)) {
            userDataCache.removeUsersCache(chatId);
            List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

            String ack = ParseQueryData.parseYesOrNo(callbackQuery);
            if (ack.equals("YES")) {
                log.debug("Попытка переместить фильм[] в [WATCHEDLIST]");
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
