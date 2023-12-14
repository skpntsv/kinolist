package ru.nsu.kinolist.bot.handlers.callbackquery;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/**
 * Format of CALLBACKDATA for playlists - COMMAND|OPERATION|ACK|FILM_ID
 * COMMAND - название плейлиста
 * OPERATION - операция над плейлистом
 * ACK - подтверждение операции
 * FILM_ID - id фильма, с которым будет проводиться выбранная операция  ** DEPRECATED - теперь мы используешь оперативку для хранения последнего фильма
 *
 * Format of CALLBACKDATA for random - в стадии разработки...
 *
 * @author skpntsv
 */
@Service
public class ParseQueryData {
    public static CallbackQueryType parseQueryType(CallbackQuery callbackQuery) {
        return CallbackQueryType.valueOf(callbackQuery.getData().split("\\|")[0]);
    }
    public static String parseListRandom(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }
    public static boolean hasRandomList(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|").length > 1;
    }
    public static String parseOperation(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[1];
    }
    public static String parseYesOrNo(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|")[2];
    }
    public static int parseFilmId(CallbackQuery callbackQuery) {
        return Integer.parseInt(callbackQuery.getData().split("\\|")[3]);
    }
    public static String createCallbackData(String... parts) {
        return String.join("|", parts);
    }
    public static boolean hasOperation(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|").length > 1;
    }
    public static boolean hasAck(CallbackQuery callbackQuery) {
        return callbackQuery.getData().split("\\|").length > 2;
    }
}
