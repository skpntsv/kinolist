package ru.nsu.kinolist.bot.handlers.callbackquery;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.WishlistService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.List;

@Slf4j
@Component
public class WishListQueryHandle implements CallbackQueryHandler{
    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.WISHLIST;

    private final WishlistService wishlistService;
    private final UserDataCache userDataCache;

    public WishListQueryHandle(WishlistService wishlistService, UserDataCache userDataCache) {
        this.wishlistService = wishlistService;
        this.userDataCache = userDataCache;
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
                        String ack = ParseQueryData.parseYesOrNo(callbackQuery);
                        if (ack.equals("YES")) {
                            // PLAYLIST|COMMAND|ACK|FILMID
                            // TODO - считываем айди фильма и добавляем в наш список

                            if (wishlistService.addMovie(chatId, ParseQueryData.parseFilmId(callbackQuery))) {
                                return List.of(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно добавлен"));
                            } else {
                                return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                            }
                        } else if (ack.equals("NO")) {
                            log.debug("The chatId [{}] rejected adding a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());

                            userDataCache.setUsersCurrentBotState(chatId, BotState.SHOW_MAIN_MENU); // TODO нужно сделать возврат на главную страницу

                            return List.of(MessagesService.createMessageTemplate(chatId, "Хорошо, не добавляем"));
                        }
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
                        String ack = ParseQueryData.parseYesOrNo(callbackQuery);
                        if (ack.equals("YES")) {
                            log.debug("Попытка удалить фильм[] из [WISHLIST]");
                            // PLAYLIST|COMMAND|ACK|FILMID
                            // TODO - считываем айди фильма и удаляем из нашего списка

                            if (wishlistService.removeMovie(chatId, ParseQueryData.parseFilmId(callbackQuery))) {
                                return List.of(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно удален"));
                            } else {
                                return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                            }
                        } else if (ack.equals("NO")) {
                            log.debug("The chatId [{}] rejected adding a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());

                            userDataCache.setUsersCurrentBotState(chatId, BotState.SHOW_MAIN_MENU); // TODO нужно сделать возврат на главную страницу

                            return List.of(MessagesService.createMessageTemplate(chatId, "Хорошо, не удаляем"));
                        }
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
                        String ack = ParseQueryData.parseYesOrNo(callbackQuery);
                        if (ack.equals("YES")) {
                            log.debug("Попытка удалить фильм[] из [WISHLIST]");
                            // PLAYLIST|COMMAND|ACK|FILMID
                            // TODO - считываем айди фильма и удаляем из нашего списка

                            if (wishlistService.transferMovie(chatId, ParseQueryData.parseFilmId(callbackQuery))) {
                                return List.of(MessagesService.createMessageTemplate(chatId, "Фильм/сериал успешно перенесен в ваш список просмотренных"));
                            } else {
                                return List.of(MessagesService.createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз"));
                            }
                        } else if (ack.equals("NO")) {
                            log.debug("The chatId [{}] rejected adding a movie, show WISHLIST again", callbackQuery.getMessage().getChatId());

                            userDataCache.setUsersCurrentBotState(chatId, BotState.SHOW_MAIN_MENU); // TODO нужно сделать возврат на главную страницу

                            return List.of(MessagesService.createMessageTemplate(chatId, "Хорошо, не переносим"));
                        }
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
