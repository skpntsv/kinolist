package ru.nsu.kinolist.bot.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;
import ru.nsu.kinolist.bot.handlers.callbackquery.ParseQueryData;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.WishlistService;
import ru.nsu.kinolist.bot.util.BotState;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class WishListHandler implements InputMessageHandler {
    private final WishlistService wishlistService;
    private final UserDataCache userDataCache;

    public WishListHandler(WishlistService wishlistService, UserDataCache userDataCache) {
        this.wishlistService = wishlistService;
        this.userDataCache = userDataCache;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleMessage(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.WISHLIST;
    }

    private List<PartialBotApiMethod<? extends Serializable>> processUsersInput(Message message) {
        BotState currentBotState = userDataCache.getUsersCurrentBotState(message.getChatId());
        String chatId = String.valueOf(message.getChatId());
        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();
        switch (currentBotState) {
            case WISHLIST_ADD -> {
                String film = message.getText();
                SendMessage sendMessage = new SendMessage(chatId, "Ты действительно хочешь добавить " + film + " в свой список желаний?");
                String callBackDataOnYes = ParseQueryData.createCallbackData(CallbackQueryType.WISHLIST.name(),
                        CallbackQueryType.ADD.name(),
                        CallbackQueryType.YES.name(),
                        "12345");
                String callBackDataOnNo =  ParseQueryData.createCallbackData(CallbackQueryType.WISHLIST.name(),
                        CallbackQueryType.ADD.name(),
                        CallbackQueryType.NO.name());
                sendMessage.setReplyMarkup(MessagesService.createYesOrNoButton(callBackDataOnYes, callBackDataOnNo));

                messages.add(sendMessage);
            }
            case WISHLIST_TRANSFER -> {
                String film = message.getText();
                SendMessage sendMessage = new SendMessage(chatId, "Ты действительно хочешь переместить " + film + " в свой список просмотренных?");
                String callBackDataOnYes = ParseQueryData.createCallbackData(CallbackQueryType.WISHLIST.name(),
                        CallbackQueryType.TRANSFER.name(),
                        CallbackQueryType.YES.name(),
                        "12345");
                String callBackDataOnNo =  ParseQueryData.createCallbackData(CallbackQueryType.WISHLIST.name(),
                        CallbackQueryType.TRANSFER.name(),
                        CallbackQueryType.NO.name());
                sendMessage.setReplyMarkup(MessagesService.createYesOrNoButton(callBackDataOnYes, callBackDataOnNo));

                messages.add(sendMessage);
            }
            case WISHLIST_REMOVE -> {
                String film = message.getText();
                SendMessage sendMessage = new SendMessage(chatId, "Ты действительно хочешь удалить " + film + " из своего желаний?");
                String callBackDataOnYes = ParseQueryData.createCallbackData(CallbackQueryType.WISHLIST.name(),
                        CallbackQueryType.REMOVE.name(),
                        CallbackQueryType.YES.name(),
                        "12345");
                String callBackDataOnNo =  ParseQueryData.createCallbackData(CallbackQueryType.WISHLIST.name(),
                        CallbackQueryType.REMOVE.name(),
                        CallbackQueryType.NO.name());
                sendMessage.setReplyMarkup(MessagesService.createYesOrNoButton(callBackDataOnYes, callBackDataOnNo));

                messages.add(sendMessage);
            }
            default -> {
                log.error("Что-то пошло не так при обратно текста пользователя в состоянии {}", currentBotState);
                messages.add(new SendMessage(chatId, "Что-то пошло не так, попробуйте ещё раз"));
            }
        }

        userDataCache.setUsersCurrentBotState(message.getChatId(), BotState.IDLE);

        return messages;
    }
}
