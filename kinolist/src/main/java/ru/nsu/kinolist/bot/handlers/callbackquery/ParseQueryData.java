package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
public class ParseQueryData {
    public static CallbackQueryType parseQueryType(CallbackQuery callbackQuery) {
        return CallbackQueryType.valueOf(callbackQuery.getData().split("\\|")[0]);
    }

    public static String parseOperation(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }
    public static String parseYesOrNo(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[2];
    }
}
