package ru.nsu.kinolist.bot.handlers.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.service.MainMenuService;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.WishlistService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WishListQueryHandle implements CallbackQueryHandler{
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.WISHLIST;

    private final WishlistService wishlistService;
    private final UserDataCache userDataCache;
    private final MainMenuService mainMenuService;

    public WishListQueryHandle(WishlistService wishlistService, UserDataCache userDataCache, MainMenuService mainMenuService) {
        this.wishlistService = wishlistService;
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

        // PLAYLIST
        if (!ParseQueryData.hasOperation(callbackQuery)) {
            log.info("Send WISHLIST menu to chatId [{}]", chatId);

            return wishlistService.getWishListMessage(chatId, callbackQuery.getMessage().getMessageId());
        }
        // PLAYLIST|COMMAND
        if (ParseQueryData.hasOperation(callbackQuery)) {
            String operation = ParseQueryData.parseOperation(callbackQuery);
            switch (operation) {
                case "ADD" -> {
                    log.debug("Transaction Processing [{}] in CallBackData [{}]",
                            operation,
                            callbackQuery.getMessage());
                    if (ParseQueryData.hasAck(callbackQuery)) {
                        userDataCache.removeUsersCache(chatId);     // удаляем состояние этого юзера, освобождаем память
                        List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

                        String ack = ParseQueryData.parseYesOrNo(callbackQuery);
                        if (ack.equals("YES")) {
                            // PLAYLIST|COMMAND|ACK|FILMID
                            if (wishlistService.addMovie(chatId, ParseQueryData.parseFilmId(callbackQuery))) {
                                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно добавлен"));
                            } else {
                                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                            }
                        } else if (ack.equals("NO")) {
                            log.debug("The chatId [{}] rejected adding a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());

                            mainMenuService.getMainMenuMessage(chatId);

                            resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не добавляем"));
                        }

                        return resultMessages;
                    } else {
                        userDataCache.setUsersCurrentBotState(chatId, BotState.WISHLIST_ADD);

                        return wishlistService.sendSearchMovie(chatId, callbackQuery.getMessage().getMessageId());
                    }
                }
                case "REMOVE" -> {
                    log.debug("Transaction Processing [{}] in CallBackData [{}]",
                            operation,
                            callbackQuery.getMessage());

                    if (ParseQueryData.hasAck(callbackQuery)) {
                        userDataCache.removeUsersCache(chatId);     // удаляем состояние этого юзера, освобождаем память
                        List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

                        String ack = ParseQueryData.parseYesOrNo(callbackQuery);
                        if (ack.equals("YES")) {
                            log.debug("Попытка удалить фильм[] из [WISHLIST]");
                            // PLAYLIST|COMMAND|ACK|FILMID
                            if (wishlistService.removeMovie(chatId, ParseQueryData.parseFilmId(callbackQuery))) {
                                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно удален"));
                            } else {
                                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                            }
                        } else if (ack.equals("NO")) {
                            log.debug("The chatId [{}] rejected adding a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());

                            resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не удаляем"));
                        }

                        return resultMessages;
                    } else {
                        userDataCache.setUsersCurrentBotState(chatId, BotState.WISHLIST_REMOVE);

                        return wishlistService.sendWriteIDMovie(chatId);
                    }
                }
                case "TRANSFER" -> {
                    log.debug("Transaction Processing [{}] in CallBackData [{}]",
                            operation,
                            callbackQuery.getMessage());

                    if (ParseQueryData.hasAck(callbackQuery)) {
                        userDataCache.removeUsersCache(chatId);     // удаляем состояние этого юзера, освобождаем память
                        List<PartialBotApiMethod<? extends Serializable>> resultMessages = new ArrayList<>();

                        String ack = ParseQueryData.parseYesOrNo(callbackQuery);
                        if (ack.equals("YES")) {
                            log.debug("Попытка удалить фильм[] из [WISHLIST]");
                            // PLAYLIST|COMMAND|ACK|FILMID
                            if (wishlistService.transferMovie(chatId, ParseQueryData.parseFilmId(callbackQuery))) {
                                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно перенесен в ваш список просмотренных"));
                            } else {
                                resultMessages.add(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                            }
                        } else if (ack.equals("NO")) {
                            log.debug("The chatId [{}] rejected adding a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());

                            resultMessages.add(MessagesService.createMessageTemplate(chatId, "Хорошо, не переносим"));
                        }

                        return resultMessages;
                    } else {
                        userDataCache.setUsersCurrentBotState(chatId, BotState.WISHLIST_TRANSFER);

                        return wishlistService.sendWriteIDMovie(chatId);
                    }
                }
                default -> log.error("Operation [{}] in callback [{}] not found from chatId [{}]",
                        operation,
                        callbackQuery.getMessage(),
                        chatId);
            }
        }

        log.error("CallBackData [{}] has not processed from chatId[{}]", callbackQuery.getData(), chatId);
        return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
    }
}
