package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import ru.nsu.kinolist.bot.service.MessagesService;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import static ru.nsu.kinolist.bot.handlers.callbackquery.ParseQueryData.parseQueryType;

@Component
public class CallbackQueryHandlerFactory {
    private final List<CallbackQueryHandler> callbackQueryHandlers;
    public CallbackQueryHandlerFactory(List<CallbackQueryHandler> callbackQueryHandlers) {
        this.callbackQueryHandlers = callbackQueryHandlers;
    }

    public List<PartialBotApiMethod<? extends Serializable>> processCallbackQuery(CallbackQuery queryType) {
        CallbackQueryType usersQueryType = parseQueryType(queryType);

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().
                filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).findFirst();

        return queryHandler.map(handler -> handler.handleCallbackQuery(queryType)).
                orElse(List.of(MessagesService.createMessageTemplate(queryType.getMessage().getChatId(), "Что-то пошло не так, попробуйте ещё раз")));
    }
}
