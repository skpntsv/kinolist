package ru.nsu.kinolist.bot.handlers;

import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.nsu.kinolist.bot.cache.UserDataCache;
import ru.nsu.kinolist.bot.handlers.callbackquery.CallbackQueryType;
import ru.nsu.kinolist.bot.service.MessagesService;
import ru.nsu.kinolist.bot.service.PlayListService;
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
public abstract class PlayListHandler {
    protected PlayListService playListService;
    protected UserDataCache userDataCache;

    public PlayListHandler(PlayListService playListService, UserDataCache userDataCache) {
        this.playListService = playListService;
        this.userDataCache = userDataCache;
    }

    protected void processAddOperation(String filmName, Long chatId, List<PartialBotApiMethod<? extends Serializable>> messages, CallbackQueryType playlist) {
        Optional<Film> movie = playListService.searchMovieByName(filmName);
        if (movie.isPresent()) {
            Film film = movie.get();

            messages.addAll(sendMovie(chatId, film, playlist));
            userDataCache.setCurrentMovieListOfUser(chatId, Collections.singletonList(film));
        } else {
            messages.add(MessagesService.createMessageTemplate(chatId, "Фильм/сериал не найден"));
        }
    }

    protected SendMessage processRemoveOperation(String filmNumber, Long chatId, CallbackQueryType playlist) {
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
        } catch (IndexOutOfBoundsException E) {
            return MessagesService.createMessageTemplate(chatId, "Введите номер, который указан перед названием фильма");
        }

        sendMessage.setText("Ты действительно хочешь удалить " + film.getFilmName() + " из своего списка желаемых?");
        sendMessage.setReplyMarkup(MessagesService.createYesOrNoButton(playlist, CallbackQueryType.REMOVE));

        return sendMessage;
    }

    protected SendMessage handleDefaultCase(BotState currentBotState, Long chatId) {
        log.error("Что-то пошло не так при обработки текста пользователя в состоянии {}", currentBotState);
        return createMessageTemplate(chatId, "Что-то пошло не так, попробуйте ещё раз");
    }

    private List<PartialBotApiMethod<? extends Serializable>> sendMovie(Long chatId, Film film, CallbackQueryType playlist) {
        // Отправка изображения фильма
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(chatId.toString());
        sendPhoto.setPhoto(new InputFile(film.getUrl()));

        //Отправка описания фильма
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.enableMarkdown(true);
        sendMessage.setText(FilmMessageBuilder.buildFilmString(film));
        sendMessage.setReplyMarkup(MessagesService.createYesOrNoButton(playlist, CallbackQueryType.ADD));

        return List.of(sendPhoto, sendMessage);
    }
}
