package ru.nsu.kinolist.bot.handlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.WatchedListService;
import ru.nsu.kinolist.bot.util.BotState;
import ru.nsu.kinolist.bot.util.FilmMessageBuilder;
import ru.nsu.kinolist.database.entities.Film;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static ru.nsu.kinolist.bot.service.MessagesService.createMessageTemplate;

@Slf4j
@Component
public class WatchedListHandler implements InputMessageHandler {
    private final WatchedListService watchedListService;
    private final UserDataCache userDataCache;

    public WatchedListHandler(WatchedListService watchedListService, UserDataCache userDataCache) {
        this.watchedListService = watchedListService;
        this.userDataCache = userDataCache;
    }

    @Override
    public List<PartialBotApiMethod<? extends Serializable>> handleMessage(Message message) {
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.WATCHEDLIST;
    }

    private List<PartialBotApiMethod<? extends Serializable>> processUsersInput(Message message) {
        BotState currentBotState = userDataCache.getUsersCurrentBotState(message.getChatId());
        Long chatId = message.getChatId();
        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        switch (currentBotState) {
            case WATCHEDLIST_ADD -> processAddOperation(message.getText(), chatId, messages);
            case WATCHEDLIST_REMOVE -> messages.add(processRemoveOperation(message.getText(), chatId));
            default -> messages.add(handleDefaultCase(currentBotState, chatId));
        }

        userDataCache.removeUsersCache(message.getChatId());    // очистка памяти
        return messages;
    }

    private void processAddOperation(String filmName, Long chatId, List<PartialBotApiMethod<? extends Serializable>> messages) {
        Optional<Film> movie = watchedListService.searchMovieByName(filmName);
        if (movie.isPresent()) {
            Film film = movie.get();

            messages.addAll(sendMovie(chatId, film, CallbackQueryType.ADD));
            userDataCache.setCurrentMovieListOfUser(chatId, Collections.singletonList(film));
        } else {
            messages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал не найден"));
        }
    }

    private SendMessage processRemoveOperation(String filmNumber, Long chatId) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        List<Film> movies;
        Film film;
        try {
            movies = userDataCache.getAndRemoveCurrentMovieListOfUser(chatId);
            film = movies.get(Integer.parseInt(filmNumber) - 1);

            userDataCache.setCurrentMovieListOfUser(chatId, Collections.singletonList(film));
        } catch (NullPointerException e) {
            return MessagesService.errorMessage(chatId);
        } catch (NumberFormatException e) {
            return MessagesService.createMessageTemplate(chatId, "Нужно вводить цифры! Попробуйте ещё раз");
        }

        sendMessage.setText("Ты действительно хочешь удалить " + film.getFilmName() + " из своего списка желаемых?");
        sendMessage.setReplyMarkup(MessagesService.createYesOrNoButton(CallbackQueryType.WATCHEDLIST, CallbackQueryType.REMOVE, film.getFilmId()));

        return sendMessage;
    }

    private SendMessage handleDefaultCase(BotState currentBotState, Long chatId) {
        log.error("Что-то пошло не так при обработки текста пользователя в состоянии {}", currentBotState);
        return createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз");
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendMovie(Long chatId, Film film, CallbackQueryType operation) {
        List<PartialBotApiMethod<? extends Serializable>> messages = new ArrayList<>();

        // Отправка изображения фильма
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(film.getUrl()));
        messages.add(sendPhoto);

        //Отправка описания фильма
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        sendMessage.setText(FilmMessageBuilder.buildFilmString(film));
        sendMessage.setReplyMarkup(MessagesService.createYesOrNoButton(CallbackQueryType.WATCHEDLIST, operation, film.getFilmId()));
        messages.add(sendMessage);

        return messages;
    }
}
