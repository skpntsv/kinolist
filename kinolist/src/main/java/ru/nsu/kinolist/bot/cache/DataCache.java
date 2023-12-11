package ru.nsu.kinolist.bot.cache;

import ru.nsu.kinolist.bot.Movie;
import ru.nsu.kinolist.bot.util.BotState;

import java.util.List;

/**
 * interface for saving statement for each user
 */
public interface DataCache {
    void setUsersCurrentBotState(Long chatId, BotState botState);
    BotState getUsersCurrentBotState(Long chatId);
    void saveSearchFoundedMovies(Long chatId, List<Movie> foundTrains);
    List<Movie> getSearchFoundedMovies(Long chatId);
}
