package ru.nsu.kinolist.bot.cache;

import org.springframework.stereotype.Service;
import ru.nsu.kinolist.bot.util.BotState;
import ru.nsu.kinolist.database.entities.Film;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserDataCache {
    private final Map<Long, BotState> usersBotStates = new ConcurrentHashMap<>();
    private final Map<Long, List<Film>> movieListOfUser = new ConcurrentHashMap<>();

    public void setUsersCurrentBotState(Long chatId, BotState botState) {
        usersBotStates.put(chatId, botState);
    }

    public BotState getUsersCurrentBotState(Long chatId) {
        BotState botState = usersBotStates.get(chatId);
        if (botState == null) {
            botState = BotState.IDLE;
        }

        return botState;
    }

    public void removeUsersCache(Long chatId) {
        usersBotStates.remove(chatId);
    }

    public void setCurrentMovieListOfUser(Long chatId, List<Film> movies) {
        movieListOfUser.put(chatId, movies);
    }

    public List<Film> getAndRemoveCurrentMovieListOfUser(Long chatId) {
        return movieListOfUser.remove(chatId);
    }

    public void removeCurrentMovieListOfUser(Long chatId) {
        movieListOfUser.remove(chatId);
    }
}