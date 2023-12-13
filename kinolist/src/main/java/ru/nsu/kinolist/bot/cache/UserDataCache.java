package ru.nsu.kinolist.bot.cache;

import org.springframework.stereotype.Service;
import ru.nsu.kinolist.bot.Film;
import ru.nsu.kinolist.bot.util.BotState;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserDataCache implements DataCache {
    private final Map<Long, BotState> usersBotStates = new ConcurrentHashMap<>();
    private final Map<Long, Film> foundedFilm = new ConcurrentHashMap<>();

    @Override
    public void setUsersCurrentBotState(Long chatId, BotState botState) {
        usersBotStates.put(chatId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(Long chatId) {
        BotState botState = usersBotStates.get(chatId);
        if (botState == null) {
            botState = BotState.IDLE;
        }

        return botState;
    }

    @Override
    public void saveSearchFoundedMovies(Long chatId, Film film) {
        foundedFilm.put(chatId, film);
    }

    @Override
    public Optional<Film> getSearchFoundedMovies(Long chatId) {
        Film film = foundedFilm.get(chatId);

        return Optional.ofNullable(film);
    }

}