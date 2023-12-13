package ru.nsu.kinolist.bot.cache;

import ru.nsu.kinolist.bot.util.BotState;

/**
 * interface for saving statement for each user
 */
public interface DataCache {
    void setUsersCurrentBotState(Long chatId, BotState botState);
    BotState getUsersCurrentBotState(Long chatId);
}
