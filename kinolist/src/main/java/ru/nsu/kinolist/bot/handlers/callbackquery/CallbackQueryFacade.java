package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.service.MessagesService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.nsu.kinolist.bot.handlers.callbackquery.ParseQueryData.parseQueryType;

@Component
public class CallbackQueryFacade {
    private final List<CallbackQueryHandler> callbackQueryHandlers;
    public CallbackQueryFacade(List<CallbackQueryHandler> callbackQueryHandlers) {
        this.callbackQueryHandlers = callbackQueryHandlers;
    }

    public List<PartialBotApiMethod<? extends Serializable>> processCallbackQuery(CallbackQuery usersQuery) {
        CallbackQueryType usersQueryType = parseQueryType(usersQuery);

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().
                filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).findFirst();

        return queryHandler.map(handler -> handler.handleCallbackQuery(usersQuery)).
                orElse(List.of(MessagesService.createMessageTemplate(usersQuery.getMessage().getChatId(), "Что-то пошло не так, попробуйте ещё раз")));
    }
}
