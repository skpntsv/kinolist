package ru.nsu.kinolist.bot.cache;

import org.springframework.stereotype.Service;
import ru.nsu.kinolist.bot.Movie;
import ru.nsu.kinolist.bot.util.BotState;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new ConcurrentHashMap<>();
    private final Map<Long, List<Movie>> searchFoundedTrains = new ConcurrentHashMap<>();

    @Override
    public void setUsersCurrentBotState(Long chatId, BotState botState) {
        usersBotStates.put(chatId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(Long chatId) {
        BotState botState = usersBotStates.get(chatId);
        if (botState == null) {
            botState = BotState.SHOW_MAIN_MENU;
        }

        return botState;
    }

    @Override
    public void saveSearchFoundedMovies(Long chatId, List<Movie> foundTrains) {
        searchFoundedTrains.put(chatId, foundTrains);
    }

    @Override
    public List<Movie> getSearchFoundedMovies(Long chatId) {
        List<Movie> foundedTrains = searchFoundedTrains.get(chatId);

        return Objects.isNull(foundedTrains) ? Collections.emptyList() : foundedTrains;
    }

}