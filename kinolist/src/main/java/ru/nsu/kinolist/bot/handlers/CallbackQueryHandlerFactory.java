package ru.nsu.kinolist.bot.handlers;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.util.MessagesService;
import ru.nsu.kinolist.bot.util.CallbackQueryType;
import ru.nsu.kinolist.bot.util.ParseQueryData;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

@Component
public class CallbackQueryHandlerFactory {
    private final List<CallbackQueryHandler> callbackQueryHandlers;
    public CallbackQueryHandlerFactory(List<CallbackQueryHandler> callbackQueryHandlers) {
        this.callbackQueryHandlers = callbackQueryHandlers;
    }

    public List<PartialBotApiMethod<? extends Serializable>> processCallbackQuery(CallbackQuery queryType) {
        CallbackQueryType usersQueryType = ParseQueryData.parseQueryType(queryType);

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().
                filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).findFirst();

        return queryHandler.map(handler -> handler.handleCallbackQuery(queryType)).
                orElse(List.of(MessagesService.createMessageTemplate(queryType.getMessage().getChatId(), "Что-то пошло не так, попробуйте ещё раз")));
    }
}
