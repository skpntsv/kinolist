package ru.nsu.kinolist.bot.cache;

import ru.nsu.kinolist.bot.Film;
import ru.nsu.kinolist.bot.util.BotState;

import java.util.List;
import java.util.Optional;

/**
 * interface for saving statement for each user
 */
public interface DataCache {
    void setUsersCurrentBotState(Long chatId, BotState botState);
    BotState getUsersCurrentBotState(Long chatId);
}
