package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class CallbackQueryFacade {
//    private ReplyMessagesService messagesService;
    private List<CallbackQueryHandler> callbackQueryHandlers;
//
    public CallbackQueryFacade(List<CallbackQueryHandler> callbackQueryHandlers) {
        this.callbackQueryHandlers = callbackQueryHandlers;
    }

    public List<PartialBotApiMethod<? extends Serializable>> processCallbackQuery(CallbackQuery usersQuery) {
        CallbackQueryType usersQueryType = CallbackQueryType.valueOf(usersQuery.getData().split("\\|")[0]);

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().
                filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).findFirst();

        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();
        return queryHandler.map(handler -> handler.handleCallbackQuery(usersQuery)).
                orElse(messages);
    }
}
